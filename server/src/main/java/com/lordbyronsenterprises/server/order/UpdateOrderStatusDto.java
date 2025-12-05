package com.lordbyronsenterprises.server.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusDto {
    @NotNull(message = "Status is required")
    private OrderStatus status;
}

