package com.lordbyronsenterprises.server.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;
import java.util.HashSet;

@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull
    @PositiveOrZero(message = "Quantity cannot be negative")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "product_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ProductTag> tags = new HashSet<>();

    @Data
    public static class ProductDto {
        private Long id;

        @NotBlank(message = "Product name is required")
        private String name;

        private String description;

        @NotNull(message = "Category ID is required")
        private Long categoryId;

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        private Double price;

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        private Integer quantity;
    }

    @Data
    public static class CategoryDto {
        private Long id;

        @NotBlank(message = "Category name is required")
        private String name;

        private String description;

    }
}
