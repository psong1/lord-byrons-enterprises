package com.lordbyronsenterprises.server.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantDto {
    private Long id;

    @NotNull
    private Long productId;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}
