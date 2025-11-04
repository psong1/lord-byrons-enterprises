package com.lordbyronsenterprises.server.order;

import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull
    private Long productId;

    private Long variantId;

    @NotBlank
    private String name;

    private String sku;

    @NotNull
    private BigDecimal unitPrice;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private BigDecimal lineSubtotal;

    @NotNull
    private BigDecimal lineDiscount;

    @NotNull
    BigDecimal lineTax;

    @NotNull
    private BigDecimal lineTotal;

    public void recalculateTotals() {
        if (quantity != null && quantity > 0 && unitPrice != null) {
            this.lineTotal = unitPrice.multiply(new BigDecimal(quantity));
        } else {
            this.lineTotal = BigDecimal.ZERO;
        }
    }
}
