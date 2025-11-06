package com.lordbyronsenterprises.server.order;

import com.lordbyronsenterprises.server.payment.PaymentException;
import com.lordbyronsenterprises.server.user.User;
import java.util.List;

public interface OrderService {

    OrderDto createOrder(User user, CreateOrderRequestDto requestDto) throws PaymentException;
    List<OrderDto> getOrdersForUser(User user);
    OrderDto getOrderById(User user, Long orderId);
}
