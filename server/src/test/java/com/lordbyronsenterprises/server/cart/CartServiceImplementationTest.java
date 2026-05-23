package com.lordbyronsenterprises.server.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.lordbyronsenterprises.server.inventory.InventoryItemRepository;
import com.lordbyronsenterprises.server.inventory.InventoryService;
import com.lordbyronsenterprises.server.inventory.OutOfStockException;
import com.lordbyronsenterprises.server.product.Product;
import com.lordbyronsenterprises.server.product.ProductRepository;
import com.lordbyronsenterprises.server.product.ProductVariant;
import com.lordbyronsenterprises.server.product.ProductVariantRepository;
import com.lordbyronsenterprises.server.user.User;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplementationTest {
    
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductVariantRepository variantRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryItemRepository inventoryItemRepository;
    @Mock
    private CartMapper cartMapper;
    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private CartServiceImplementation cartService;

    private User testUser;
    private Cart testCart;
    private ProductVariant testVariant;
    private AddCartItemDto addItemDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testCart = new Cart();
        testCart.setId(10L);
        testCart.setUser(testUser);
        testCart.setItems(new ArrayList<>());

        Product testProduct = new Product();
        testProduct.setId(100L);

        testVariant = new ProductVariant();
        testVariant.setId(200L);
        testVariant.setProduct(testProduct);
        testVariant.setPrice(new BigDecimal("50.00"));

        addItemDto = new AddCartItemDto();
        addItemDto.setVariantId(200L);
        addItemDto.setQuantity(2);
    }

    @Test
    void addItemToCart_SufficientStock_AddItemAndRecalculates() {
        // Arrange
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(variantRepository.findById(200L)).thenReturn(Optional.of(testVariant));
        when(cartItemRepository.findByCartAndVariant(testCart, testVariant)).thenReturn(Optional.empty());

        // Mock that we have 10 items in stock
        when(inventoryService.getAvailableStock(testVariant)).thenReturn(10);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(cartMapper.toDto(any(Cart.class))).thenReturn(new CartDto());

        // Act
        cartService.addItemToCart(testUser, null, addItemDto);

        // Assert
        assertEquals(1, testCart.getItems().size()); // Item was addede to the cart list
        CartItem addedItem = testCart.getItems().get(0);
        assertEquals(2, addedItem.getQuantity());
        assertEquals(new BigDecimal("100.00"), addedItem.getLineTotal());

        // Cart totals should be recalculated
        assertEquals(new BigDecimal("100.00"), testCart.getSubtotal());
        assertEquals(new BigDecimal("8.00"), testCart.getTax());
        verify(cartRepository, times(1)).save(testCart);
    }

    @Test
    void addItemToCart_InsufficientStock_ThrowsOutOfStockException() {
        // Arrange
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(variantRepository.findById(200L)).thenReturn(Optional.of(testVariant));
        when(cartItemRepository.findByCartAndVariant(testCart, testVariant)).thenReturn(Optional.empty());

        // Mock that we only have 1 item in stock, but the user wants 2
        when(inventoryService.getAvailableStock(testVariant)).thenReturn(1);

        // Act & Assert
        assertThrows(OutOfStockException.class, () -> {
            cartService.addItemToCart(testUser, null, addItemDto);
        });

        // Ensure the cart was never saved due to the exception
        verify(cartRepository, never()).save(any());
    }
}
