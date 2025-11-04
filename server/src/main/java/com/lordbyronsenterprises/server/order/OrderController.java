package com.lordbyronsenterprises.server.order;

import com.lordbyronsenterprises.server.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateOrderRequestDto orderRequest
    ) {
        OrderDto createdOrder = orderService.createOrder(user, orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getMyOrders(
            @AuthenticationPrincipal User user
    ) {
        List<OrderDto> orders = orderService.getOrdersForUser(user);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getMyOrderById(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId
    ) {
        OrderDto order = orderService.getOrderById(user, orderId);
        return ResponseEntity.ok(order);
    }
}
