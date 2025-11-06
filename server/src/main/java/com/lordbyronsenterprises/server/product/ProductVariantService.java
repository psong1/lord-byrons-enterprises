package com.lordbyronsenterprises.server.product;

import java.util.List;

public interface ProductVariantService {
    List<ProductVariantDto> getVariantsForProduct(Long productId);
    ProductVariantDto createVariant(Long variantId, ProductVariantDto variantDto);
    ProductVariantDto updateVariant(Long variantId, ProductVariantDto variantDto);
    void deleteVariant(Long variantId);
}
