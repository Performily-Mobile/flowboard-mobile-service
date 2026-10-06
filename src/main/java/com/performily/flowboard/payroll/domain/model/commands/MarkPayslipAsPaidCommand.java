package com.performily.flowboard.payroll.domain.model.commands;

import java.time.LocalDate;

/**
 * Mark Payslip As Paid Command
 * @summary
 * Command to mark the payment of a payslip as paid.
 *
 * @since 1.0.0
 */
public record MarkPayslipAsPaidCommand(Long payslipId, LocalDate paidOn) {
    /**
     * Compact constructor for MarkPayslipAsPaidCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public MarkPayslipAsPaidCommand {
        if (payslipId == null || payslipId <= 0) {
            throw new IllegalArgumentException("payslipId cannot be null or less than 1");
        }
        if (paidOn == null) {
            throw new IllegalArgumentException("paidOn is required");
        }
    }
}
