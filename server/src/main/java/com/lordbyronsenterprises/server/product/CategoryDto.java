package com.lordbyronsenterprises.server.product;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CategoryDto {
    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
}
