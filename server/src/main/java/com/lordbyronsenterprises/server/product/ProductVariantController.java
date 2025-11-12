package com.lordbyronsenterprises.server.product;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductVariantDto> createVariant(
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariantDto variantDto) {
        try {
            if ((productId == null || productId <= 0)) {
                return ResponseEntity.badRequest().build();
            }

            ProductVariantDto createdVariant = variantService.createVariant(productId, variantDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVariant);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductVariantDto> updateVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantDto variantDto
    ) {
        try {
            if (productId == null || productId <= 0 || variantId == null || variantId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            ProductVariantDto updatedVariant = variantService.updateVariant(variantId, variantDto);
            return ResponseEntity.ok(updatedVariant);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId
    ) {
        try {
            if (productId == null || productId <= 0 || variantId == null || variantId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            variantService.deleteVariant(variantId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }
}
