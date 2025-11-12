package com.lordbyronsenterprises.server.cart;

import com.lordbyronsenterprises.server.inventory.OutOfStockException;
import com.lordbyronsenterprises.server.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    @GetMapping
    public ResponseEntity<CartDto> getMyCart(@AuthenticationPrincipal User user) {
        CartDto cartDto = cartService.getCartForUser(user);
        return ResponseEntity.ok(cartDto);
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItemToCart(@AuthenticationPrincipal User user, @Valid @RequestBody AddCartItemDto addItemDto) {
        try {
            CartDto updatedCart = cartService.addItemToCart(user, addItemDto);
            return ResponseEntity.ok(updatedCart);
        } catch (OutOfStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartDto> updateItemQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemDto updateItemDto) {
        CartDto updatedCart = cartService.updateItemQuantity(user, cartItemId, updateItemDto.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartDto> deleteItemFromCart(
            @AuthenticationPrincipal User user,
            @PathVariable Long cartItemId
    ) {
        CartDto updatedCart = cartService.deleteItemFromCart(user, cartItemId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}
