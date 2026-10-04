package com.performily.flowboard.payroll.interfaces.rest.resources;

public record PayslipResource(
        Long id,
        String period, 
        String issueDate,
        String netAmount,
        String publicationStatus,
        String paymentStatus
) {}