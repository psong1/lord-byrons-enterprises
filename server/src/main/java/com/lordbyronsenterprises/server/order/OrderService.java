package com.lordbyronsenterprises.server.order;

import java.util.List;

import com.lordbyronsenterprises.server.payment.PaymentException;
import com.lordbyronsenterprises.server.user.User;

public interface OrderService {

    OrderDto createOrder(User user, CreateOrderRequestDto requestDto) throws PaymentException;
    List<OrderDto> getOrdersForUser(User user);
    OrderDto getOrderById(User user, Long orderId);
    List<OrderDto> getAllOrders();
    OrderDto updateOrderStatus(Long orderId, OrderStatus status);
}
