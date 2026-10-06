package com.performily.flowboard.payroll.domain.model.commands;

/**
 * Publish Payslip Command
 * @summary
 * Command to publish a payslip so the employee can see it.
 *
 * @since 1.0.0
 */
public record PublishPayslipCommand(Long payslipId) {
    /**
     * Compact constructor for PublishPayslipCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public PublishPayslipCommand {
        if (payslipId == null || payslipId <= 0) {
            throw new IllegalArgumentException("payslipId cannot be null or less than 1");
        }
    }
}
