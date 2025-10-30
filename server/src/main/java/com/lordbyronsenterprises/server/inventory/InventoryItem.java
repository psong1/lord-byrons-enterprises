package com.lordbyronsenterprises.server.inventory;

import com.lordbyronsenterprises.server.product.ProductVariant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false, unique = true)
    private ProductVariant productVariant;

    @NotNull
    @Min(0)
    private Integer onHand = 0;

    @NotNull
    @Min(0)
    private Integer reserved = 0;

    private Integer reorderLevel;

    @Column(nullable = false)
    private Instant updatedAt;

    @Transient
    public Integer getAvailable() {
        return this.onHand - this.reserved;
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
