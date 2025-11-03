package com.lordbyronsenterprises.server.inventory;

import com.lordbyronsenterprises.server.product.ProductVariant;

public interface InventoryService {
    void reserveStock(ProductVariant variant, int quantity);
    void commitStock(ProductVariant variant, int quantity);
    void releaseStock(ProductVariant variant, int quantity);
}

class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message) {
        super(message);
    }
}