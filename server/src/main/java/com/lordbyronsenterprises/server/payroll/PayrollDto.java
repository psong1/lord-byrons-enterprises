package com.lordbyronsenterprises.server.payroll;

import java.time.Instant;
import java.util.List;

import lombok.Data;

@Data
public class PayrollDto {
    private Long id;
    private Instant payPeriodStart;
    private Instant payPeriodEnd;
    private PayrollStatus status;
    private int paycheckCount;
    private List<PaycheckDto> paychecks;
}
