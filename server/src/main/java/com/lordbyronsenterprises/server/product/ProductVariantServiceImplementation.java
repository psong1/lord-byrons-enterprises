package com.lordbyronsenterprises.server.product;

import com.lordbyronsenterprises.server.inventory.InventoryItem;
import com.lordbyronsenterprises.server.inventory.InventoryItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.owasp.encoder.Encode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductVariantServiceImplementation implements ProductVariantService {

    private final ProductVariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final ProductVariantMapper variantMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantDto> getVariantsForProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found with id: " + productId);
        }
        return variantRepository.findByProductId(productId).stream()
                .map(variantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductVariantDto createVariant(Long productId, ProductVariantDto variantDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        ProductVariant variant = variantMapper.toEntity(variantDto, product);

        variant.setSku(Encode.forHtml(variantDto.getSku()));
        variant.setTitle(Encode.forHtml(variantDto.getTitle()));

        ProductVariant savedVariant = variantRepository.save(variant);

        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setProductVariant(savedVariant);
        inventoryItem.setOnHand(variant.getQuantity());
        inventoryItem.setReserved(0);
        inventoryItemRepository.save(inventoryItem);

        return variantMapper.toDto(savedVariant);
    }

    @Override
    public ProductVariantDto updateVariant(Long variantId, ProductVariantDto variantDto) {
        ProductVariant existingVariant = variantRepository.findById(variantId)
                .orElseThrow(() -> new EntityNotFoundException("Product variant not found with id: " + variantId));


        existingVariant.setSku(Encode.forHtml(variantDto.getSku()));
        existingVariant.setTitle(Encode.forHtml(variantDto.getTitle()));
        existingVariant.setPrice(variantDto.getPrice());
        existingVariant.setQuantity(variantDto.getQuantity());

        InventoryItem item = inventoryItemRepository.findByProductVariantId(variantId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory item not found"));
        item.setOnHand(variantDto.getQuantity());
        inventoryItemRepository.save(item);

        ProductVariant updatedVariant = variantRepository.save(existingVariant);
        return variantMapper.toDto(updatedVariant);
    }

    @Override
    public void deleteVariant(Long variantId) {
        if (!variantRepository.existsById(variantId)) {
            throw new EntityNotFoundException("Product variant not found with id: " + variantId);
        }

        inventoryItemRepository.findByProductVariantId(variantId).ifPresent(inventoryItemRepository::delete);

        variantRepository.deleteById(variantId);
    }
}