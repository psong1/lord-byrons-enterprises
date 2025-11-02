package com.lordbyronsenterprises.server.cart;

import com.lordbyronsenterprises.server.product.Product;
import com.lordbyronsenterprises.server.product.ProductRepository;
import com.lordbyronsenterprises.server.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImplementation implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Override
    public CartDto getCartForUser(User user) {
        Cart cart = getOrCreateCart(user);
        return cartMapper.toDto(cart);
    }

    @Override
    public CartDto addItemToCart(User user, AddCartItemDto itemDto) {
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + itemDto.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(itemDto.getQuantity());
            newItem.setUnitPrice(product.getPrice());
            cart.getItems().add(newItem);
        }

        recalculateCart(cart);
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
            cartItemRepository.save(item);
        }

        recalculateCart(cart);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Override
    public CartDto deleteItemFromCart(User user, Long cartItemId) {
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(cartItemId).
                orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        cart.getItems().remove(item);

        recalculateCart(cart);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Override
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    private void recalculateCart(Cart cart) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            item.recalculateLineTotal();
            subtotal = subtotal.add(item.getLineTotal());
        }
        cart.setSubtotal(subtotal);

        BigDecimal tax = subtotal.multiply(new BigDecimal("0.08"));
        cart.setTax(tax);

        cart.setTotal(subtotal.add(tax));
    }
}
