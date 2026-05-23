package com.lordbyronsenterprises.server.payroll;

import org.springframework.stereotype.Component;

@Component
public class PaycheckMapper {
    public PaycheckDto toDto(Paycheck pc) {
        PaycheckDto dto = new PaycheckDto();
        dto.setId(pc.getId());
        dto.setPayrollId(pc.getPayroll().getId());
        dto.setPayPeriodEnd(pc.getPayroll().getPayPeriodEnd());
        dto.setEmployeeId(pc.getEmployee().getId());
        dto.setEmployeeName(pc.getEmployee().getFirstName() + " " + pc.getEmployee().getLastName());
        dto.setGrossPay(pc.getGrossPay());
        dto.setDeductions(pc.getDeductions());
        dto.setNetPay(pc.getNetPay());
        dto.setStatus(pc.getStatus());
        return dto;
    }
}
