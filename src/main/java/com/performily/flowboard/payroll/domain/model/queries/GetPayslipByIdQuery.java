package com.performily.flowboard.payroll.domain.model.queries;

/**
 * Get Payslip By Id Query
 * @summary
 * Query to get any payslip by id (HR view).
 *
 * @since 1.0.0
 */
public record GetPayslipByIdQuery(Long payslipId) {
    /**
     * Compact constructor for GetPayslipByIdQuery.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public GetPayslipByIdQuery {
        if (payslipId == null || payslipId <= 0) {
            throw new IllegalArgumentException("payslipId cannot be null or less than 1");
        }
    }
}
