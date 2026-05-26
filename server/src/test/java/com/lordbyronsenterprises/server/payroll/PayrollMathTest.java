package com.lordbyronsenterprises.server.payroll;

import com.lordbyronsenterprises.server.user.Role;
import com.lordbyronsenterprises.server.user.User;
import com.lordbyronsenterprises.server.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PayrollMathTest {
    
    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private PaycheckRepository paycheckRepository;
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private PaycheckMapper paycheckMapper;

    @InjectMocks
    private PayrollServiceImplementation payrollService;

    @Test
    void testBahamianNibDeductionCalculatesCorrectly() {
        User admin = new User();
        admin.setRole(Role.ADMIN);

        User employee = new User();
        employee.setId(1L);
        employee.setRole(Role.EMPLOYEE);

        ProcessPayrollRequestDto request = new ProcessPayrollRequestDto();
        request.setPayPeriodEnd(LocalDate.now());
        request.setEmployeeIds(List.of(1L));

        when(userRepository.findAllById(request.getEmployeeIds())).thenReturn(List.of(employee));
        when(payrollRepository.save(any(Payroll.class))).thenAnswer(i -> i.getArguments()[0]);
        payrollService.processPayroll(admin, request);

        ArgumentCaptor<Payroll> payrollCaptor = ArgumentCaptor.forClass(Payroll.class);
        verify(payrollRepository).save(payrollCaptor.capture());
        verifyNoInteractions(paycheckRepository);

        Paycheck savedCheck = payrollCaptor.getValue().getPaychecks().get(0);

        assertEquals(new BigDecimal("600.00"), savedCheck.getGrossPay(), "Gross pay should be $600.00");
        assertEquals(new BigDecimal("20.40"), savedCheck.getDeductions(), "NIB deduction should be exactly 3.4%");
        assertEquals(new BigDecimal("579.60"), savedCheck.getNetPay(), "Net pay should be $579.60");
        assertEquals(PaycheckStatus.PENDING, savedCheck.getStatus(), "New paychecks must default to PENDING");
    }
}
