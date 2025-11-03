package com.lordbyronsenterprises.server.product;

import com.lordbyronsenterprises.server.user.User;
import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.Instant;

@Data
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "Rating is required")
    @Min(value = 1, message = "Review must be at least 1 star")
    @Max(value = 5, message = "Review can be at most 5 stars")
    private Integer rating;

    private String title;

    private String comment;

    @PastOrPresent(message = "Creation date cannot be in the future")
    private Instant createdAt;

    @PastOrPresent(message = "Update date cannot be in the future")
    private Instant updatedAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
