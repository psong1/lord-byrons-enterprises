package com.lordbyronsenterprises.server.dto.category;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class CategoryDto {
    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

}
