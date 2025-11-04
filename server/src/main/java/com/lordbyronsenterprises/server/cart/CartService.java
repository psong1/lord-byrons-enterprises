package com.lordbyronsenterprises.server.cart;

import com.lordbyronsenterprises.server.user.User;

public interface CartService {
    CartDto getCartForUser(User user);
    CartDto addItemToCart(User user, AddCartItemDto itemDto);
    CartDto updateItemQuantity(User user, Long cartItemId, int quantity);
    CartDto deleteItemFromCart(User user, Long cartItemId);

    Cart getCartEntityForUser(User user);

    // Call by OrderService after an order is placed
    void clearCart(User user);
}
