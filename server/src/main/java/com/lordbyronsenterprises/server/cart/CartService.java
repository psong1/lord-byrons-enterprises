package com.lordbyronsenterprises.server.cart;

import com.lordbyronsenterprises.server.user.User;

public interface CartService {
    CartDto getCartForUser(User user, String guestSessionToken);

    CartDto addItemToCart(User user, String guestSessionToken, AddCartItemDto itemDto);

    CartDto updateItemQuantity(User user, String guestSessionToken, Long cartItemId, int quantity);

    CartDto deleteItemFromCart(User user, String guestSessionToken, Long cartItemId);

    Cart getCartEntityForUser(User user);

    void clearCart(User user, String guestSessionToken);

    /**
     * Moves all lines from the guest cart keyed by {@code guestSessionToken} into the user's cart,
     * then deletes the guest cart. No-op if there is no guest cart for that token.
     */
    void mergeGuestCartIntoUser(User user, String guestSessionToken);
}
