package com.lordbyronsenterprises.server.payment;

import com.lordbyronsenterprises.server.order.Order;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImplementation implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.secret.key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    @Transactional
    public Payment charge(Order order, String paymentMethodId) throws PaymentException {
        long amountInCents = order.getGrandTotal().multiply(new BigDecimal("100")).longValue();

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getGrandTotal());
        payment.setCurrency("usd");
        payment.setPaymentMethod(paymentMethodId);

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(order.getCurrency().toLowerCase())
                    .setPaymentMethod(paymentMethodId)
                    .setConfirm(true)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                    .build()
                    ).build();

            PaymentIntent intent = PaymentIntent.create(params);

            if ("succeeded".equals(intent.getStatus())) {
                payment.setStatus(PaymentStatus.SUCCEEDED);
                payment.setTransactionId(intent.getId());
                return paymentRepository.save(payment);
            } else {
                String failureReason = intent.getLastPaymentError() != null ? intent.getLastPaymentError().getMessage() : "Payment failed for unknown reason";
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason(failureReason);
                paymentRepository.save(payment);
                throw new PaymentException(failureReason);
            }
        } catch (StripeException e ) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);
            throw new PaymentException(e.getMessage());
        }
    }
}
