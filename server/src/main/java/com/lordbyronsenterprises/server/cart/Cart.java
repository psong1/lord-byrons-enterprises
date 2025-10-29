package com.lordbyronsenterprises.server.cart;

import com.lordbyronsenterprises.server.user.User;
import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Date;

@Entity
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = true, unique = true)
    private User user;

    @Column(name = "session_token", unique = true, length = 128)
    private String sessionToken;

    private Double subtotal;
    private Double tax;
    private Double total;

    @PastOrPresent(message = "Creation date cannot be in the future")
    private Date createdAt;

    @PastOrPresent(message = "Update date cannot be in the future")
    private Date updatedAt;

    @AssertTrue(message = "Cart must be associated with a user or have a session token")
    private boolean isUserOrSessionTokenPresent() {
        return user != null || (sessionToken != null && !sessionToken.isBlank());
    }

    public boolean isGuest() {
        return user == null && sessionToken == null;
    }

    public boolean isAuthenticatedCart() {
        return user != null;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    private void onUpdate() { this.updatedAt = new Date(); }
}