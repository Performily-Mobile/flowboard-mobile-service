package com.performily.flowboard.payroll.interfaces.rest.resources;

public record PayslipDetailResource(
        Long id,
        String headerTitle,
        String issuedDateText,
        String paymentStatus,
        String companyInfo,
        String employeeName,
        String period,
        String totalIncomes,
        String totalDeductions,
        String netAmount
) {}