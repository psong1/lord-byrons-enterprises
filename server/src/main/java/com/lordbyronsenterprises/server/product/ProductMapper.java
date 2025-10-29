package com.lordbyronsenterprises.server.product;

import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product.ProductDto toDto(Product product) {
        Product.ProductDto dto = new Product.ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategoryId(product.getCategory().getId());
        dto.setQuantity(product.getQuantity());
        return dto;
    }

    public Product toEntity(Product.ProductDto dto, Category category) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(category);
        product.setQuantity(dto.getQuantity());
        return product;
    }
}
