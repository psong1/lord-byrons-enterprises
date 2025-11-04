package com.lordbyronsenterprises.server.order;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private Long orderNumber;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal taxTotal;
    private BigDecimal grandTotal;
    private Instant placedAt;
    private AddressSnapshot shippingAddress;
    private List<OrderItemDto> items;
}
