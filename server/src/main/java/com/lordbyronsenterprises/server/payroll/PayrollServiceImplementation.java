package com.lordbyronsenterprises.server.payroll;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lordbyronsenterprises.server.user.Role;
import com.lordbyronsenterprises.server.user.User;
import com.lordbyronsenterprises.server.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PayrollServiceImplementation implements PayrollService {

    private final PayrollRepository payrollRepository;
    private final PaycheckRepository paycheckRepository;
    private final UserRepository userRepository;
    private final PaycheckMapper paycheckMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PaycheckDto> getPaychecksForCurrentUser(User user) {
        return paycheckRepository.findByEmployeeIdOrderByPayroll_PayPeriodEndDesc(user.getId())
                .stream()
                .map(paycheckMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollDto> getAllPayrolls() {
        return payrollRepository.findAll().stream()
                .map(this::toPayrollDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaycheckDto> getPaychecksByPayrollId(Long payrollId) {
        return paycheckRepository.findByPayrollId(payrollId).stream()
                .map(paycheckMapper::toDto)
                .toList();
    }

    @Override
    public PayrollDto processPayroll(User admin, ProcessPayrollRequestDto request) {
        Instant periodEnd = request.getPayPeriodEnd().atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant periodStart = periodEnd.minus(14, ChronoUnit.DAYS);

        Payroll payroll = new Payroll();
        payroll.setPayPeriodStart(periodStart);
        payroll.setPayPeriodEnd(periodEnd);
        payroll.setProcessedBy(admin);
        payroll.setStatus(PayrollStatus.PROCESSED);

        List<User> employees = resolveEmployees(request.getEmployeeIds());
        for (User emp : employees) {
            Paycheck pc = buildPaycheck(emp, payroll);
            payroll.getPaychecks().add(pc);
            pc.setPayroll(payroll);
        }

        Payroll saved = payrollRepository.save(payroll);
        return toPayrollDto(saved);
    }

    /** null or empty employeeIds → all EMPLOYEE users; otherwise only those IDs with EMPLOYEE role */
    private List<User> resolveEmployees(List<Long> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return userRepository.findByRole(Role.EMPLOYEE);
        }
        return userRepository.findAllById(employeeIds).stream()
                .filter(user -> user.getRole() == Role.EMPLOYEE)
                .toList();
    }

    private Paycheck buildPaycheck(User emp, Payroll payroll) {
        BigDecimal gross = new BigDecimal("2000.00");
        BigDecimal deductions = gross.multiply(new BigDecimal("0.22"));
        Paycheck pc = new Paycheck();
        pc.setEmployee(emp);
        pc.setPayroll(payroll);
        pc.setGrossPay(gross);
        pc.setDeductions(deductions);
        pc.setNetPay(gross.subtract(deductions));
        pc.setStatus(PaycheckStatus.PENDING);
        return pc;
    }

    private PayrollDto toPayrollDto(Payroll payroll) {
        PayrollDto dto = new PayrollDto();
        dto.setId(payroll.getId());
        dto.setPayPeriodStart(payroll.getPayPeriodStart());
        dto.setPayPeriodEnd(payroll.getPayPeriodEnd());
        dto.setStatus(payroll.getStatus());
        dto.setPaycheckCount(payroll.getPaychecks().size());
        dto.setPaychecks(payroll.getPaychecks().stream()
                .map(paycheckMapper::toDto)
                .toList());
        return dto;
    }
}
