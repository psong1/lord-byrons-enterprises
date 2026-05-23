package com.lordbyronsenterprises.server.payroll;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaycheckRepository extends JpaRepository<Paycheck, Long>{
    List<Paycheck> findByEmployeeIdOrderByPayroll_PayPeriodEndDesc(Long employeeId);
    List<Paycheck> findByPayrollId(Long payrollId);
}
