package com.lordbyronsenterprises.server.cart;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lordbyronsenterprises.server.inventory.InventoryItem;
import com.lordbyronsenterprises.server.inventory.InventoryItemRepository;
import com.lordbyronsenterprises.server.inventory.InventoryService;
import com.lordbyronsenterprises.server.inventory.OutOfStockException;
import com.lordbyronsenterprises.server.product.Product;
import com.lordbyronsenterprises.server.product.ProductRepository;
import com.lordbyronsenterprises.server.product.ProductVariant;
import com.lordbyronsenterprises.server.product.ProductVariantRepository;
import com.lordbyronsenterprises.server.user.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImplementation implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final CartMapper cartMapper;
    private final InventoryService inventoryService;
    private final InventoryItemRepository inventoryItemRepository;

    @Override
    public CartDto getCartForUser(User user, String guestSessionToken) {
        Cart cart = resolveCart(user, guestSessionToken);
        return cartMapper.toDto(cart);
    }

    @Override
    public CartDto addItemToCart(User user, String guestSessionToken, AddCartItemDto itemDto) {
        Cart cart = resolveCart(user, guestSessionToken);

        ProductVariant variant;

        if (itemDto.getVariantId() != null) {
            variant = variantRepository.findById(itemDto.getVariantId())
                    .orElseThrow(() -> new EntityNotFoundException("Product variant not found"));

            if (itemDto.getProductId() != null && !variant.getProduct().getId().equals(itemDto.getProductId())) {
                throw new IllegalArgumentException("Product ID does not match the variant's product");
            }
        } else if (itemDto.getProductId() != null) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            List<ProductVariant> existingVariants = variantRepository.findByProductId(product.getId());

            if (existingVariants.isEmpty()) {
                variant = createDefaultVariant(product);
            } else {
                variant = existingVariants.get(0);
            }
        } else {
            throw new IllegalArgumentException("Either variantId or productId must be provided");
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndVariant(cart, variant);

        int quantityAlreadyInCart = existingItemOpt.map(CartItem::getQuantity).orElse(0);
        int requestedTotalQuantity = quantityAlreadyInCart + itemDto.getQuantity();
        int availableStock = inventoryService.getAvailableStock(variant);

        if (requestedTotalQuantity > availableStock) {
            throw new OutOfStockException(
                    "Cannot add " + itemDto.getQuantity() + " item(s). Only " +
                            availableStock + " are available and you already have " +
                            quantityAlreadyInCart + " in your cart."
            );
        }

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + itemDto.getQuantity());
            item.setUnitPrice(variant.getPrice());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(variant.getProduct());
            newItem.setVariant(variant);
            newItem.setQuantity(itemDto.getQuantity());
            newItem.setUnitPrice(variant.getPrice());
            cart.getItems().add(newItem);
        }

        recalculateCart(cart);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Override
    public CartDto updateItemQuantity(User user, String guestSessionToken, Long cartItemId, int quantity) {
        Cart cart = resolveCart(user, guestSessionToken);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("Cannot modify item in another user's cart");
        }

        if (quantity <= 0) {
            return deleteItemFromCart(user, guestSessionToken, cartItemId);
        }
        item.setQuantity(quantity);
        item.setUnitPrice(item.getVariant().getPrice());
        cartItemRepository.save(item);

        recalculateCart(cart);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Override
    public CartDto deleteItemFromCart(User user, String guestSessionToken, Long cartItemId) {
        Cart cart = resolveCart(user, guestSessionToken);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("Cannot modify item in another user's cart");
        }

        cart.getItems().remove(item);

        recalculateCart(cart);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Override
    public Cart getCartEntityForUser(User user) {
        return getOrCreateUserCart(user);
    }

    @Override
    public void clearCart(User user, String guestSessionToken) {
        Cart cart;
        if (user != null) {
            cart = getOrCreateUserCart(user);
        } else {
            if (guestSessionToken == null || guestSessionToken.isBlank()) {
                throw new IllegalArgumentException("Guest session token required to clear cart");
            }
            cart = cartRepository.findBySessionToken(guestSessionToken)
                    .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        }
        cart.getItems().clear();
        recalculateCart(cart);
        cartRepository.save(cart);
    }

    @Override
    public void mergeGuestCartIntoUser(User user, String guestSessionToken) {
        if (user == null) {
            throw new IllegalArgumentException("User must be authenticated to merge carts");
        }
        if (guestSessionToken == null || guestSessionToken.isBlank()) {
            return;
        }
        Optional<Cart> guestCartOpt = cartRepository.findBySessionToken(guestSessionToken);
        if (guestCartOpt.isEmpty()) {
            return;
        }
        Cart guestCart = guestCartOpt.get();
        if (guestCart.getUser() != null) {
            return;
        }
        List<CartItem> items = new ArrayList<>(guestCart.getItems());
        if (items.isEmpty()) {
            cartRepository.delete(guestCart);
            return;
        }
        for (CartItem guestItem : items) {
            AddCartItemDto dto = new AddCartItemDto();
            dto.setVariantId(guestItem.getVariant().getId());
            dto.setQuantity(guestItem.getQuantity());
            addItemToCart(user, null, dto);
        }
        cartRepository.delete(guestCart);
    }

    private Cart resolveCart(User user, String guestSessionToken) {
        if (user != null) {
            return getOrCreateUserCart(user);
        }
        if (guestSessionToken == null || guestSessionToken.isBlank()) {
            throw new IllegalArgumentException("Guest shopping requires an active browser session");
        }
        return getOrCreateGuestCart(guestSessionToken);
    }

    private Cart getOrCreateUserCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    private Cart getOrCreateGuestCart(String sessionToken) {
        return cartRepository.findBySessionToken(sessionToken)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setSessionToken(sessionToken);
                    return cartRepository.save(newCart);
                });
    }

    private ProductVariant createDefaultVariant(Product product) {
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku("DEFAULT-" + product.getId());
        variant.setTitle(product.getName() + " - Default");
        variant.setPrice(product.getPrice());
        Integer productQuantity = product.getQuantity();
        variant.setQuantity(productQuantity != null ? productQuantity : 0);
        variant.setCreatedAt(Instant.now());
        variant.setUpdatedAt(Instant.now());

        ProductVariant savedVariant = variantRepository.save(variant);

        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setProductVariant(savedVariant);
        inventoryItem.setOnHand(variant.getQuantity());
        inventoryItem.setReserved(0);
        inventoryItem.setUpdatedAt(Instant.now());
        inventoryItemRepository.save(inventoryItem);

        return savedVariant;
    }

    private void recalculateCart(Cart cart) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            item.recalculateLineTotal();
            subtotal = subtotal.add(item.getLineTotal());
        }
        cart.setSubtotal(subtotal);

        BigDecimal taxRate = new BigDecimal("0.08");
        BigDecimal tax = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        cart.setTax(tax);

        cart.setTotal(subtotal.add(tax).setScale(2, RoundingMode.HALF_UP));
    }
}
