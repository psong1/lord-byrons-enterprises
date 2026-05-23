package com.lordbyronsenterprises.server.payroll;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.lordbyronsenterprises.server.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "payrolls")
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Instant payPeriodStart;

    @NotNull
    private Instant payPeriodEnd;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PayrollStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_user_id")
    private User processedBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "payroll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paycheck> paychecks = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        if (status == null) status = PayrollStatus.DRAFT;
    }
}
