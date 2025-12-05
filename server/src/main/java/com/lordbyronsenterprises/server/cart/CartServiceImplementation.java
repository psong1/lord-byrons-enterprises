package com.lordbyronsenterprises.server.cart;

import java.math.BigDecimal;
import java.time.Instant;
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
    public CartDto getCartForUser(User user) {
        Cart cart = getOrCreateCart(user);
        return cartMapper.toDto(cart);
    }

    @Override
    public CartDto addItemToCart(User user, AddCartItemDto itemDto) {
        if (user == null) {
            throw new IllegalArgumentException("User must be authenticated to add items to cart");
        }
        Cart cart = getOrCreateCart(user);

        ProductVariant variant;
        
        // If variantId is provided, use it
        if (itemDto.getVariantId() != null) {
            variant = variantRepository.findById(itemDto.getVariantId())
                    .orElseThrow(() -> new EntityNotFoundException("Product variant not found"));
            
            // If productId was also provided, verify it matches the variant's product
            if (itemDto.getProductId() != null && !variant.getProduct().getId().equals(itemDto.getProductId())) {
                throw new IllegalArgumentException("Product ID does not match the variant's product");
            }
        } else if (itemDto.getProductId() != null) {
            // If no variantId but productId is provided, get or create a default variant
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));
            
            // Check if product has any variants
            List<ProductVariant> existingVariants = variantRepository.findByProductId(product.getId());
            
            if (existingVariants.isEmpty()) {
                // Create a default variant for this product
                variant = createDefaultVariant(product);
            } else {
                // Use the first available variant
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
        
        // Ensure user is still set before saving (validation requirement)
        if (cart.getUser() == null) {
            cart.setUser(user);
        }
        
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Override
    public CartDto updateItemQuantity(User user, Long cartItemId, int quantity) {
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("Cannot modify item in another user's cart");
        }

        if (quantity <= 0) {
            return deleteItemFromCart(user, cartItemId);
        } else {
            item.setQuantity(quantity);
            item.setUnitPrice(item.getVariant().getPrice());
            cartItemRepository.save(item);
        }

        recalculateCart(cart);
        
        // Ensure user is still set before saving (validation requirement)
        if (cart.getUser() == null) {
            cart.setUser(user);
        }
        
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Override
    public CartDto deleteItemFromCart(User user, Long cartItemId) {
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(cartItemId).
                orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("Cannot modify item in another user's cart");
        }

        cart.getItems().remove(item);

        recalculateCart(cart);
        
        // Ensure user is still set before saving (validation requirement)
        if (cart.getUser() == null) {
            cart.setUser(user);
        }
        
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Override
    public Cart getCartEntityForUser(User user) {
        return getOrCreateCart(user);
    }

    @Override
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        recalculateCart(cart);
        
        // Ensure user is still set before saving (validation requirement)
        if (cart.getUser() == null) {
            cart.setUser(user);
        }
        
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null when creating or retrieving cart");
        }
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    // Ensure user is set before validation
                    if (newCart.getUser() == null) {
                        throw new IllegalStateException("Failed to set user on cart");
                    }
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
        
        // Create inventory item for the variant
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
        BigDecimal tax = subtotal.multiply(taxRate);
        cart.setTax(tax);

        cart.setTotal(subtotal.add(tax));
    }
}
