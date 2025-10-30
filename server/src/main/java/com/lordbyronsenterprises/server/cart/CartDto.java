package com.lordbyronsenterprises.server.cart;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class CartDto {
    private Long id;
    private List<CartItemDto> items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
}
