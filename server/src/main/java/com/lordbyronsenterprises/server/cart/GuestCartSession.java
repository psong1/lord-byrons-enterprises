package com.lordbyronsenterprises.server.cart;

/**
 * HTTP session attribute holding the opaque token that matches {@link Cart#getSessionToken()}
 * for anonymous carts.
 */
public final class GuestCartSession {

    public static final String SESSION_ATTR_CART_TOKEN = "guestCartSessionToken";

    private GuestCartSession() {
    }
}
