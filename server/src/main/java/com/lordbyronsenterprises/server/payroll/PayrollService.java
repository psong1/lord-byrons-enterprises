package com.lordbyronsenterprises.server.payroll;

import java.util.List;

import com.lordbyronsenterprises.server.user.User;

public interface PayrollService {
    List<PaycheckDto> getPaychecksForCurrentUser(User user);
    List<PayrollDto> getAllPayrolls();
    List<PaycheckDto> getPaychecksByPayrollId(Long payrollId);
    PayrollDto processPayroll(User admin, ProcessPayrollRequestDto request);
    PaycheckDto disbursePaycheck(Long paycheckId);
}
