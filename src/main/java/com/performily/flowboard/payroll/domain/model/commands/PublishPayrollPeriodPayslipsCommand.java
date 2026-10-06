package com.performily.flowboard.payroll.domain.model.commands;

/**
 * Publish Payroll Period Payslips Command
 * @summary
 * Command to publish every payslip of a payroll period that is still UNDER_REVIEW.
 *
 * @since 1.0.0
 */
public record PublishPayrollPeriodPayslipsCommand(Long payrollPeriodId) {
    /**
     * Compact constructor for PublishPayrollPeriodPayslipsCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public PublishPayrollPeriodPayslipsCommand {
        if (payrollPeriodId == null || payrollPeriodId <= 0) {
            throw new IllegalArgumentException("payrollPeriodId cannot be null or less than 1");
        }
    }
}
