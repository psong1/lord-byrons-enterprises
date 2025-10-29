package com.lordbyronsenterprises.server.model;

import com.lordbyronsenterprises.server.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id", nullable = false)
    private User actor;

    @NotBlank
    private String action;

    @NotBlank
    private String entityType;

    @NotNull
    private Long entityId;

    @Column(columnDefinition = "JSON")
    private String beforeState;

    @Column(columnDefinition = "JSON")
    private String afterState;

    @Column(nullable = false, updatable = false)
    private Instant occuredAt;

    @PrePersist
    protected void onCreate() {
        this.occuredAt = Instant.now();
    }
}
