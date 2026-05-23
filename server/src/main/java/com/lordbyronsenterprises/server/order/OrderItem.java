package com.lordbyronsenterprises.server.order;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
            this.lineTotal = unitPrice.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
        } else {
            this.lineTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
    }
}
