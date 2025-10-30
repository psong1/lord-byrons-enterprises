package com.lordbyronsenterprises.server.cart;

import com.lordbyronsenterprises.server.product.Product;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartDto toDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartDto dto = new CartDto();
        dto.setId(cart.getId());

        // Map each CartItem entity to CartItemDto
        if (cart.getItems() != null) {
            dto.setItems(cart.getItems().stream()
                    .map(this::toItemDto)
                    .collect(Collectors.toList())
            );
        }

        dto.setSubtotal(convertToBigDecimal(cart.getSubtotal()));
        dto.setTax(convertToBigDecimal(cart.getTax()));
        dto.setTotal(convertToBigDecimal(cart.getTotal()));

        return dto;
    }

    public CartItemDto toItemDto(CartItem item) {
        if (item == null) {
            return null;
        }

        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());

        Product product = item.getProduct();
        if (product != null) {
            dto.setProductId(product.getId());
            dto.setProductName(product.getName());
        }

        dto.setUnitPrice(convertToBigDecimal(item.getUnitPrice()));
        dto.setLineTotal(convertToBigDecimal(item.getLineTotal()));

        return dto;
    }

    private BigDecimal convertToBigDecimal(Double value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
