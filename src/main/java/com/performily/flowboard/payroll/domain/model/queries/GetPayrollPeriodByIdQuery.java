package com.performily.flowboard.payroll.domain.model.queries;

/**
 * Get Payroll Period By Id Query
 * @summary
 * Query to get a payroll period by id.
 *
 * @since 1.0.0
 */
public record GetPayrollPeriodByIdQuery(Long payrollPeriodId) {
    /**
     * Compact constructor for GetPayrollPeriodByIdQuery.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public GetPayrollPeriodByIdQuery {
        if (payrollPeriodId == null || payrollPeriodId <= 0) {
            throw new IllegalArgumentException("payrollPeriodId cannot be null or less than 1");
        }
    }
}
