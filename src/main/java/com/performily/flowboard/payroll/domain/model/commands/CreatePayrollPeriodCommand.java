package com.performily.flowboard.payroll.domain.model.commands;

import java.time.LocalDate;

/**
 * Create Payroll Period Command
 * @summary
 * Command to open a payroll period. There is only one period per month.
 *
 * @since 1.0.0
 */
public record CreatePayrollPeriodCommand(Integer year, Integer month, LocalDate scheduledPaymentDate) {
    /**
     * Compact constructor for CreatePayrollPeriodCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public CreatePayrollPeriodCommand {
        if (year == null || month == null) {
            throw new IllegalArgumentException("year and month are required");
        }
        if (scheduledPaymentDate == null) {
            throw new IllegalArgumentException("scheduledPaymentDate is required");
        }
    }
}
