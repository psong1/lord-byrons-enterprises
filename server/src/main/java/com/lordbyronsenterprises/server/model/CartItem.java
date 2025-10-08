package com.lordbyronsenterprises.server.model;

import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Date;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Data
@Table(name = "cart items",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cart_id", "product_id"})
})
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @Positive(message = "Quantity must be positive")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull
    @PositiveOrZero(message = "Unit price cannot be negative")
    @Column(nullable = false)
    private Double unitPrice;

    @PositiveOrZero(message = "Line subtotal must be positive")
    @Column(nullable = false)
    private Double lineSubtotal;

    @PositiveOrZero(message = "Line discount cannot be negative")
    @Column(nullable = false)
    private Double lineDiscount;

    @PositiveOrZero(message = "Line tax must be positive")
    @Column(nullable = false)
    private Double lineTax;

    @PositiveOrZero(message = "Line total must be positive")
    @Column(nullable = false)
    private Double lineTotal;

    @PastOrPresent(message = "Creation date cannot be in the future")
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @PastOrPresent(message = "Update date cannot be in the future")
    @Column(nullable = false)
    private Date updatedAt;

    @PrePersist
    private void onCreate() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
        recalcTotalsIfNeeded();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = new Date();
        recalcTotalsIfNeeded();
    }

    private void recalcTotalsIfNeeded() {
        if (quantity != null && unitPrice != null) {
            this.lineSubtotal = round(quantity * unitPrice);
        }
        if (lineSubtotal == null) {
            this.lineSubtotal = 0.0;
        }
        if (lineDiscount == null) {
            this.lineDiscount = 0.0;
        }
        if (lineTax == null) {
            this.lineTax = 0.0;
        }
        this.lineTotal = round(lineSubtotal - lineDiscount + lineTax);
        if (this.lineTotal < 0.0) {
            this.lineTotal = 0.0;
        }
    }

    private Double round(Double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
