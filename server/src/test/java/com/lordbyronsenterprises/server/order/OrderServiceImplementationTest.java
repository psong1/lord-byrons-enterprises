package com.lordbyronsenterprises.server.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lordbyronsenterprises.server.cart.Cart;
import com.lordbyronsenterprises.server.cart.CartItem;
import com.lordbyronsenterprises.server.cart.CartService;
import com.lordbyronsenterprises.server.inventory.InventoryService;
import com.lordbyronsenterprises.server.payment.PaymentException;
import com.lordbyronsenterprises.server.payment.PaymentService;
import com.lordbyronsenterprises.server.product.Product;
import com.lordbyronsenterprises.server.product.ProductVariant;
import com.lordbyronsenterprises.server.user.Address;
import com.lordbyronsenterprises.server.user.AddressRepository;
import com.lordbyronsenterprises.server.user.User;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplementationTest {
    
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartService cartService;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderServiceImplementation orderService;

    private User testUser;
    private Cart testCart;
    private Address testAddress;
    private CreateOrderRequestDto orderRequest;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testCart = new Cart();
        testCart.setItems(new ArrayList<>());
        testCart.setSubtotal(new BigDecimal("100.00"));
        testCart.setTax(new BigDecimal("8.00"));
        testCart.setTotal(new BigDecimal("108.00"));

        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setUser(testUser);
        testAddress.setLine1("123 Main St.");
        testAddress.setCity("Atlanta");

        orderRequest = new CreateOrderRequestDto();
        orderRequest.setShippingAddressId(1L);
        orderRequest.setBillingAddressId(1L);
        orderRequest.setPaymentMethodId("pm_12345");
    }

    @Test
    void createOrder_EmptyCart_ThrowsException() {
        // Arrange
        when(cartService.getCartEntityForUser(testUser)).thenReturn(testCart);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.createOrder(testUser, orderRequest);
        });

        assertEquals("Cannot create and order from an empty cart", exception.getMessage());
        verify(inventoryService, never()).reserveStock(any(), anyInt());
    }

    @Test
    void createOrder_SuccessfulTransaction_CommitsInventory() {
        // Arrange
        CartItem cartItem = new CartItem();
        Product product = new Product();
        product.setId(1L);
        ProductVariant variant = new ProductVariant();
        variant.setId(20L);
        cartItem.setProduct(product);
        cartItem.setVariant(variant);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(new BigDecimal("50.00"));
        testCart.getItems().add(cartItem);

        Order savedOrder = new Order();
        savedOrder.setId(100L);
        savedOrder.setStatus(OrderStatus.NEW);
        savedOrder.setItems(new ArrayList<>());

        when(cartService.getCartEntityForUser(testUser)).thenReturn(testCart);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toOrderDto(any(Order.class))).thenReturn(new OrderDto());

        // Act
        OrderDto result = orderService.createOrder(testUser, orderRequest);

        // Assert
        assertNotNull(result);
        verify(inventoryService, times(1)).reserveStock(variant, 2);
        verify(paymentService, times(1)).charge(savedOrder, "pm_12345");
        verify(inventoryService, times(1)).commitStock(variant, 2);
        verify(cartService, times(1)).clearCart(testUser, null);
        assertEquals(OrderStatus.PAID, savedOrder.getStatus());
    }

    @Test
    void createOrder_PaymentFails_ReleasesInventoryAndThrows() {
        // Arrange
        CartItem cartItem = new CartItem();
        cartItem.setProduct(new Product());
        ProductVariant variant = new ProductVariant();
        cartItem.setVariant(variant);
        cartItem.setQuantity(1);
        testCart.getItems().add(cartItem);

        when(cartService.getCartEntityForUser(testUser)).thenReturn(testCart);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        doThrow(new PaymentException("Card declined")).when(paymentService).charge(any(Order.class), anyString());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.createOrder(testUser, orderRequest);
        });

        assertTrue(exception.getMessage().contains("Card declined"));

        // Verify inventory was reserved, but then released upon failure
        verify(inventoryService, times(1)).reserveStock(variant, 1);
        verify(inventoryService, times(1)).releaseStock(variant, 1);
        verify(inventoryService, never()).commitStock(any(), anyInt());
        verify(cartService, never()).clearCart(any(), any());

    }

}
