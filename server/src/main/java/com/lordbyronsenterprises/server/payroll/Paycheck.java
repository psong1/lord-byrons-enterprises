package com.lordbyronsenterprises.server.payroll;

import java.math.BigDecimal;

import com.lordbyronsenterprises.server.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Entity
@Table(name = "paychecks")
public class Paycheck {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payroll_id")
    private Payroll payroll;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id")
    private User employee;

    @NotNull
    private BigDecimal grossPay;

    @NotNull
    private BigDecimal deductions;

    @NotNull
    private BigDecimal netPay;

    private BigDecimal hoursWorked;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaycheckStatus status;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = PaycheckStatus.PENDING;
        if (netPay == null && grossPay != null && deductions != null) {
            netPay = grossPay.subtract(deductions);
        }
    }
}
