package com.performily.flowboard.payroll.domain.model.queries;

/**
 * Get Payslips By Payroll Period Id Query
 * @summary
 * Query to get every payslip of a payroll period (HR view).
 *
 * @since 1.0.0
 */
public record GetPayslipsByPayrollPeriodIdQuery(Long payrollPeriodId) {
    /**
     * Compact constructor for GetPayslipsByPayrollPeriodIdQuery.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public GetPayslipsByPayrollPeriodIdQuery {
        if (payrollPeriodId == null || payrollPeriodId <= 0) {
            throw new IllegalArgumentException("payrollPeriodId cannot be null or less than 1");
        }
    }
}
