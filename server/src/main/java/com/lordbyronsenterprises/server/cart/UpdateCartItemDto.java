package com.lordbyronsenterprises.server.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemDto {
    @NotNull(message = "Quantity is required")
    @Min(value = 0)
    private Integer quantity;
}
