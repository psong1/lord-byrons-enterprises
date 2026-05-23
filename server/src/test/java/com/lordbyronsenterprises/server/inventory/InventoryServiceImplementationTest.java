package com.lordbyronsenterprises.server.inventory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lordbyronsenterprises.server.product.ProductVariant;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplementationTest {
    
    @Mock
    private InventoryItemRepository inventoryItemRepository;
    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private InventoryServiceImplementation inventoryService;

    private ProductVariant testVariant;
    private InventoryItem testInventoryItem;

    @BeforeEach
    void setUp() {
        testVariant = new ProductVariant();
        testVariant.setId(1L);
        testVariant.setSku("TEST-SKU-001");

        testInventoryItem = new InventoryItem();
        testInventoryItem.setProductVariant(testVariant);
        testInventoryItem.setOnHand(10);
        testInventoryItem.setReserved(0);
    }

    @Test
    void reserveStock_AvailableStock_SuccessfullyReservesAndLogsMovement() {
        // Arrange
        when(inventoryItemRepository.findByProductVariantId(1L)).thenReturn(Optional.of(testInventoryItem));

        // Act
        inventoryService.reserveStock(testVariant, 3);

        // Assert
        assertEquals(3, testInventoryItem.getReserved()); // Move 3 items to reserved
        assertEquals(10, testInventoryItem.getOnHand()); // On-hand remains 10 until committed
        assertEquals(7, testInventoryItem.getAvailable());

        verify(inventoryItemRepository, times(1)).save(testInventoryItem);
        verify(stockMovementRepository, times(1)).save(any(StockMovement.class));
    }

    @Test
    void reserveStock_InsufficientStock_ThrowsException() {
        // Arrange
        // User wants 3, but only 2 are available (10 on hand, 8 already reserved)
        testInventoryItem.setReserved(8);
        when(inventoryItemRepository.findByProductVariantId(1L)).thenReturn(Optional.of(testInventoryItem));

        // Act & Assert
        OutOfStockException exception = assertThrows(OutOfStockException.class, () -> {
            inventoryService.reserveStock(testVariant, 3);
        });

        assertTrue(exception.getMessage().contains("TEST-SKU-001"));
        verify(inventoryItemRepository, never()).save(any());
        verify(stockMovementRepository, never()).save(any());
    }

    @Test
    void commitStock_ValidReservation_ReducesOnHandStock() {
        // Arrange
        testInventoryItem.setReserved(5);
        when(inventoryItemRepository.findByProductVariantId(1L)).thenReturn(Optional.of(testInventoryItem));

        // Act (Payment successful, commiting the 5 reserved items)
        inventoryService.commitStock(testVariant, 5);

        // Assert
        assertEquals(0, testInventoryItem.getReserved()); // Reservation cleared
        assertEquals(5, testInventoryItem.getOnHand());   // On-hand permanently reduced from 10 to 5

        verify(inventoryItemRepository, times(1)).save(testInventoryItem);
    }
}
