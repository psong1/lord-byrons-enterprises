package com.lordbyronsenterprises.server.cart;

import com.lordbyronsenterprises.server.user.User;
import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

@Data
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = true, unique = true)
    private User user;

    @Column(name = "session_token", unique = true, length = 128)
    private String sessionToken;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal tax;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    @AssertTrue(message = "Cart must be associated with a user or have a session token")
    private boolean isUserOrSessionTokenPresent() {
        return user != null || (sessionToken != null && !sessionToken.isBlank());
    }

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    public boolean isGuest() {
        return user == null && sessionToken == null;
    }

    public boolean isAuthenticatedCart() {
        return user != null;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    private void onUpdate() { this.updatedAt = Instant.now(); }
}