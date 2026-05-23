package com.lordbyronsenterprises.server.payroll;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProcessPayrollRequestDto {
    @NotNull
    private LocalDate payPeriodEnd;
    private List<Long> employeeIds;
}
