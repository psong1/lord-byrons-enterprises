package com.lordbyronsenterprises.server.cart;

import com.lordbyronsenterprises.server.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

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
    public ResponseEntity<CartDto> addItemToCart(@AuthenticationPrincipal User user, @Valid @RequestBody AddCartItemDto addItemDto) {
        CartDto updatedCart = cartService.addItemToCart(user, addItemDto);
        return ResponseEntity.ok(updatedCart);
    }



}
