package com.lordbyronsenterprises.server.payroll;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lordbyronsenterprises.server.user.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping("/paychecks/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<PaycheckDto>> getMyPaychecks(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(payrollService.getPaychecksForCurrentUser(user));
    }

    @GetMapping("/payroll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PayrollDto>> getAllPayrolls() {
        return ResponseEntity.ok(payrollService.getAllPayrolls());
    }

    @GetMapping("/payroll/{payrollId}/paychecks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaycheckDto>> getPaychecksForPayroll(@PathVariable Long payrollId) {
        return ResponseEntity.ok(payrollService.getPaychecksByPayrollId(payrollId));
    }

    @PostMapping("/payroll/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PayrollDto> processPayroll(
            @AuthenticationPrincipal User admin,
            @Valid @RequestBody ProcessPayrollRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(payrollService.processPayroll(admin, request));
    }
}
