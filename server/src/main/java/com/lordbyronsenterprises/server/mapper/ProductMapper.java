package com.lordbyronsenterprises.server.mapper;

import com.lordbyronsenterprises.server.dto.product.ProductDto;
import com.lordbyronsenterprises.server.model.Product;
import com.lordbyronsenterprises.server.model.Category;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategoryId(product.getCategory().getId());
        dto.setQuantity(product.getQuantity());
        return dto;
    }

    public Product toEntity(ProductDto dto, Category category) {
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
