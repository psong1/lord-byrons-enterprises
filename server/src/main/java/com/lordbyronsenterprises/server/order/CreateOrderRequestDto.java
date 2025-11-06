package com.lordbyronsenterprises.server.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequestDto {
    @NotNull(message = "Shipping address ID is required")
    private Long shippingAddressId;

    @NotNull(message = "Billing address ID is required")
    private Long billingAddressId;

    @NotBlank(message = "Payment method ID is required")
    private String paymentMethodId;
}
