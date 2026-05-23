package com.lordbyronsenterprises.server.payroll;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Data;

@Data
public class PaycheckDto {
    private Long id;
    private Long payrollId;
    private Instant payPeriodEnd;
    private Long employeeId;
    private String employeeName;
    private BigDecimal grossPay;
    private BigDecimal deductions;
    private BigDecimal netPay;
    private PaycheckStatus status;
}
