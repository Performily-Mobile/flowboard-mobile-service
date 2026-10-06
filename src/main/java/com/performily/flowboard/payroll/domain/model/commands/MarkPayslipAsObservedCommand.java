package com.performily.flowboard.payroll.domain.model.commands;

/**
 * Mark Payslip As Observed Command
 * @summary
 * Command to mark the payment of a payslip as observed, with the reason.
 *
 * @since 1.0.0
 */
public record MarkPayslipAsObservedCommand(Long payslipId, String reason) {
    /**
     * Compact constructor for MarkPayslipAsObservedCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public MarkPayslipAsObservedCommand {
        if (payslipId == null || payslipId <= 0) {
            throw new IllegalArgumentException("payslipId cannot be null or less than 1");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason is required");
        }
    }
}
