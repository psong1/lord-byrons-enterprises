package com.lordbyronsenterprises.server.inventory;

import com.lordbyronsenterprises.server.product.ProductVariant;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImplementation implements InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    public void reserveStock(ProductVariant variant, int quantity) {
        InventoryItem item = getInventoryItem(variant);

        if (item.getAvailable() < quantity) {
            throw new OutOfStockException("Not enough stock for " + variant.getSku());
        }

        item.setReserved(item.getReserved() + quantity);
        inventoryItemRepository.save(item);

        createStockMovement(variant, quantity, StockMovementType.RESERVATION, "Order reservation");
    }

    @Override
    public void commitStock(ProductVariant variant, int quantity) {
        InventoryItem item = getInventoryItem(variant);

        if (item.getReserved() < quantity) {
            throw new IllegalStateException("Stock for " + variant.getSku() + " was not properly reserved");
        }
        if (item.getOnHand() < quantity) {
            throw new IllegalStateException("Stock on hand for " + variant.getSku() + " is less than commited quantity");
        }

        item.setReserved(item.getReserved() - quantity);
        item.setOnHand(item.getOnHand() - quantity);
        inventoryItemRepository.save(item);

        createStockMovement(variant, -quantity, StockMovementType.OUT, "Order sale");
    }

    @Override
    public void releaseStock(ProductVariant variant, int quantity) {
        InventoryItem item = getInventoryItem(variant);

        if (item.getReserved() < quantity) {
            item.setReserved(0);
        } else {
            item.setReserved(item.getReserved() - quantity);
        }
        inventoryItemRepository.save(item);

        createStockMovement(variant, -quantity, StockMovementType.RESERVATION, "Order failed/cancelled");
    }

    private InventoryItem getInventoryItem(ProductVariant variant) {
        return inventoryItemRepository.findByProductVariantId(variant.getId())
                .orElseGet(() -> {
                    InventoryItem newItem = new InventoryItem();
                    newItem.setProductVariant(variant);
                    newItem.setOnHand(0);
                    newItem.setReserved(0);
                    return inventoryItemRepository.save(newItem);
                });
    }

    private void createStockMovement(ProductVariant variant, int quantity, StockMovementType type, String note) {
        StockMovement movement = new StockMovement();
        movement.setVariant(variant);
        movement.setType(type);
        movement.setQuantity(quantity);
        movement.setNote(note);
        stockMovementRepository.save(movement);
    }
}
