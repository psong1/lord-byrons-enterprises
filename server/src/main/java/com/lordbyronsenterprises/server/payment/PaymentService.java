package com.lordbyronsenterprises.server.payment;

import com.lordbyronsenterprises.server.order.Order;
import com.lordbyronsenterprises.server.payment.PaymentException;

public interface PaymentService {
    Payment charge(Order order, String paymentMethodId) throws PaymentException;
}
