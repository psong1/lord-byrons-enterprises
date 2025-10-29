package com.lordbyronsenterprises.server.order;

import com.lordbyronsenterprises.server.model.AddressSnapshot;
import com.lordbyronsenterprises.server.user.User;
import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @NotBlank
    private String currency;

    @NotNull
    private BigDecimal subtotal;

    @NotNull
    private BigDecimal taxTotal;

    @NotNull
    private BigDecimal grandTotal;

    // Snapshot of customer email for guest checkouts
    private String customerEmail;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "billing_first_name")),
            @AttributeOverride(name = "lastName", column = @Column(name = "billing_last_name")),
            @AttributeOverride(name = "line1", column = @Column(name = "billing_line_1")),
            @AttributeOverride(name = "line2", column = @Column(name = "billing_line_2")),
            @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "billing_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "billing_country")),
            @AttributeOverride(name = "phone", column = @Column(name = "billing_phone"))
    })
    private AddressSnapshot billingAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "shipping_first_name")),
            @AttributeOverride(name = "lastName", column = @Column(name = "shipping_last_name")),
            @AttributeOverride(name = "line1", column = @Column(name = "shipping_line_1")),
            @AttributeOverride(name = "line2", column = @Column(name = "shipping_line_2")),
            @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "shipping_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "shipping_country")),
            @AttributeOverride(name = "phone", column = @Column(name = "shipping_phone"))
    })
    private AddressSnapshot shippingAddress;

    @Column(nullable = false, updatable = false)
    private Instant placedAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedby = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.placedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
