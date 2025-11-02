package com.lordbyronsenterprises.server.cart;

import com.lordbyronsenterprises.server.product.Product;
import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;

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
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        recalculateLineTotal();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
        recalculateLineTotal();
    }

    public void recalculateLineTotal() {
        if (quantity == null || quantity <= 0) {
            this.lineTotal = unitPrice.multiply(new BigDecimal(quantity));
        } else {
            this.lineTotal = BigDecimal.ZERO;
        }
    }

}
