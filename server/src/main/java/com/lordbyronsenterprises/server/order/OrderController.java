package com.lordbyronsenterprises.server.order;

import com.lordbyronsenterprises.server.user.User;
import com.lordbyronsenterprises.server.payment.PaymentException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateOrderRequestDto orderRequest
    ) {
        try {
            OrderDto createdOrder = orderService.createOrder(user, orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (PaymentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
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
