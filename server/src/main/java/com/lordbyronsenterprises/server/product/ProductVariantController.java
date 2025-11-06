package com.lordbyronsenterprises.server.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService variantService;

    @GetMapping
    public ResponseEntity<List<ProductVariantDto>> getVariantsForProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(variantService.getVariantsForProduct(productId));
    }

    @PostMapping
    public ResponseEntity<ProductVariantDto> createVariant(
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariantDto variantDto) {
        ProductVariantDto createdVariant = variantService.createVariant(productId, variantDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVariant);
    }

    @PutMapping("/{variantId}")
    public ResponseEntity<ProductVariantDto> updateVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantDto variantDto
    ) {
        ProductVariantDto updatedVariant = variantService.updateVariant(variantId, variantDto);
        return ResponseEntity.ok(updatedVariant);
    }

    @DeleteMapping("/{variantId}")
    public ResponseEntity<Void> deleteVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId
    ) {
        variantService.deleteVariant(variantId);
        return ResponseEntity.noContent().build();
    }
}
