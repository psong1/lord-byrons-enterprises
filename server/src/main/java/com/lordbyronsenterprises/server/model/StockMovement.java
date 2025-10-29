package com.lordbyronsenterprises.server.model;

import com.lordbyronsenterprises.server.product.ProductVariant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "stock_movements")
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant variant;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StockMovementType type;

    @NotNull
    private Integer quantity;

    private String referenceType;

    private Long referenceId;

    private String note;

    @Column(nullable = false, updatable = false)
    private Instant occuredAt;

    @PrePersist
    protected void onCreate() {
        this.occuredAt = Instant.now();
    }
}
