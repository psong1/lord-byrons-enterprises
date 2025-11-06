package com.lordbyronsenterprises.server.product;

import org.springframework.stereotype.Component;

@Component
public class ProductVariantMapper {

    public ProductVariantDto toDto(ProductVariant variant) {
        ProductVariantDto dto = new ProductVariantDto();
        dto.setId(variant.getId());
        dto.setProductId(variant.getProduct().getId());
        dto.setSku(variant.getSku());
        dto.setTitle(variant.getTitle());
        dto.setPrice(variant.getPrice());
        dto.setQuantity(variant.getQuantity());
        return dto;
    }

    public ProductVariant toEntity(ProductVariantDto dto, Product product) {
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setPrice(dto.getPrice());
        variant.setQuantity(dto.getQuantity());
        return variant;
    }
}
