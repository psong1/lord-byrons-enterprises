package com.lordbyronsenterprises.server.order;

import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public OrderItemDto toOrderItemDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setSku(item.getSku());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setLineTotal(item.getLineTotal());
        return dto;
    }

    public OrderDto toOrderDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setPlacedAt(order.getPlacedAt());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setSubtotal(order.getSubtotal());
        dto.setTaxTotal(order.getTaxTotal());
        dto.setGrandTotal(order.getGrandTotal());
        dto.setItems(order.getItems().stream()
                .map(this::toOrderItemDto)
                .collect(Collectors.toList()));
        return dto;
    }
}
