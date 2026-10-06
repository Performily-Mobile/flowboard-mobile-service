package com.performily.flowboard.payroll.domain.model.queries;

/**
 * Get Payslip By Id And Employee Id Query
 * @summary
 * Query to get a payslip of an employee. Only PUBLISHED payslips of that employee are returned.
 *
 * @since 1.0.0
 */
public record GetPayslipByIdAndEmployeeIdQuery(Long payslipId, Long employeeId) {
    /**
     * Compact constructor for GetPayslipByIdAndEmployeeIdQuery.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public GetPayslipByIdAndEmployeeIdQuery {
        if (payslipId == null || payslipId <= 0) {
            throw new IllegalArgumentException("payslipId cannot be null or less than 1");
        }
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
    }
}
