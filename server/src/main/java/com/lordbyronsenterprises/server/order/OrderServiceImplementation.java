package com.lordbyronsenterprises.server.order;

import com.lordbyronsenterprises.server.cart.Cart;
import com.lordbyronsenterprises.server.cart.CartItem;
import com.lordbyronsenterprises.server.cart.CartService;
import com.lordbyronsenterprises.server.inventory.InventoryService;
import com.lordbyronsenterprises.server.payment.PaymentService;
import com.lordbyronsenterprises.server.payment.PaymentException;
import com.lordbyronsenterprises.server.user.Address;
import com.lordbyronsenterprises.server.user.AddressRepository;
import com.lordbyronsenterprises.server.user.User;
import com.stripe.exception.StripeException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImplementation implements OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final InventoryService inventoryService;
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;

    @Override
    public OrderDto createOrder(User user, CreateOrderRequestDto orderRequest) {
        Cart cart = cartService.getCartEntityForUser(user);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create and order from an empty cart");
        }

        Address shippingAddress = findAndVerifyAddress(user, orderRequest.getShippingAddressId());
        Address billingAddress = findAndVerifyAddress(user, orderRequest.getBillingAddressId());

        for (CartItem item : cart.getItems()) {
            inventoryService.reserveStock(item.getVariant(), item.getQuantity());
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(System.nanoTime());
        order.setStatus(OrderStatus.NEW);
        order.setCurrency("USD");
        order.setSubtotal(cart.getSubtotal());
        order.setTaxTotal(cart.getTax());
        order.setGrandTotal(cart.getTotal());
        order.setCustomerEmail(user.getEmail());
        order.setShippingAddress(createSnapshot(shippingAddress));
        order.setBillingAddress(createSnapshot(billingAddress));

        Order savedOrder = orderRepository.save(order);

        for (CartItem item : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(item.getProduct().getId());
            orderItem.setVariantId(item.getVariant().getId());
            orderItem.setName(item.getVariant().getTitle());
            orderItem.setSku(item.getVariant().getSku());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getUnitPrice());
            orderItem.recalculateTotals();
            order.getItems().add(orderItem);
        }

        // Payment Processing
        try {
            paymentService.charge(savedOrder, orderRequest.getPaymentMethodId());
            savedOrder.setStatus(OrderStatus.PAID);
        } catch (PaymentException | StripeException e) {
            for (CartItem item : cart.getItems()) {
                inventoryService.releaseStock(item.getVariant(), item.getQuantity());
            }

            throw new IllegalStateException("Payment failed: " + e.getMessage());
        }

        for (CartItem item : cart.getItems()) {
            inventoryService.commitStock(item.getVariant(), item.getQuantity());
        }

        cartService.clearCart(user);

        Order finalOrder = orderRepository.save(savedOrder);
        return orderMapper.toOrderDto(finalOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersForUser(User user) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .map(orderMapper::toOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("User does not have permission to view this order");
        }

        return orderMapper.toOrderDto(order);
    }

    private Address findAndVerifyAddress(User user, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + addressId));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Address does not belong to the current user");
        }
        return address;
    }

    private AddressSnapshot createSnapshot(Address address) {
        AddressSnapshot snapshot = new AddressSnapshot();
        snapshot.setLine1(address.getLine1());
        snapshot.setLine2(address.getLine2());
        snapshot.setCity(address.getCity());
        snapshot.setCountry(address.getCountry());
        return snapshot;
    }


}
