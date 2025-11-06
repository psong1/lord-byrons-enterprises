package com.lordbyronsenterprises.server.payment;

import com.lordbyronsenterprises.server.order.Order;
import com.stripe.exception.StripeException;

public interface PaymentService {
    Payment charge(Order order, String paymentMethodId) throws StripeException;
}
