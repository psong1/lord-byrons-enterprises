package com.lordbyronsenterprises.server.cart;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lordbyronsenterprises.server.inventory.OutOfStockException;
import com.lordbyronsenterprises.server.user.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto> getMyCart(
            @AuthenticationPrincipal User user,
            HttpServletRequest request
    ) {
        String guestToken = guestSessionTokenForCart(user, request);
        CartDto cartDto = cartService.getCartForUser(user, guestToken);
        return ResponseEntity.ok(cartDto);
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItemToCart(
            @AuthenticationPrincipal User user,
            HttpServletRequest request,
            @Valid @RequestBody AddCartItemDto addItemDto
    ) {
        try {
            String guestToken = guestSessionTokenForCart(user, request);
            CartDto updatedCart = cartService.addItemToCart(user, guestToken, addItemDto);
            return ResponseEntity.ok(updatedCart);
        } catch (OutOfStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartDto> updateItemQuantity(
            @AuthenticationPrincipal User user,
            HttpServletRequest request,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemDto updateItemDto
    ) {
        String guestToken = guestSessionTokenForCart(user, request);
        CartDto updatedCart = cartService.updateItemQuantity(user, guestToken, cartItemId, updateItemDto.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartDto> deleteItemFromCart(
            @AuthenticationPrincipal User user,
            HttpServletRequest request,
            @PathVariable Long cartItemId
    ) {
        String guestToken = guestSessionTokenForCart(user, request);
        CartDto updatedCart = cartService.deleteItemFromCart(user, guestToken, cartItemId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User user, HttpServletRequest request) {
        String guestToken = guestSessionTokenForCart(user, request);
        cartService.clearCart(user, guestToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/merge")
    public ResponseEntity<?> mergeGuestCart(
            @AuthenticationPrincipal User user,
            HttpServletRequest request
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login required to merge carts");
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            String guestToken = (String) session.getAttribute(GuestCartSession.SESSION_ATTR_CART_TOKEN);
            if (guestToken != null) {
                cartService.mergeGuestCartIntoUser(user, guestToken);
                session.removeAttribute(GuestCartSession.SESSION_ATTR_CART_TOKEN);
            }
        }
        return ResponseEntity.ok(cartService.getCartForUser(user, null));
    }

    private static String guestSessionTokenForCart(User user, HttpServletRequest request) {
        if (user != null) {
            return null;
        }
        HttpSession session = request.getSession(true);
        String token = (String) session.getAttribute(GuestCartSession.SESSION_ATTR_CART_TOKEN);
        if (token == null) {
            token = UUID.randomUUID().toString();
            session.setAttribute(GuestCartSession.SESSION_ATTR_CART_TOKEN, token);
        }
        return token;
    }
}
