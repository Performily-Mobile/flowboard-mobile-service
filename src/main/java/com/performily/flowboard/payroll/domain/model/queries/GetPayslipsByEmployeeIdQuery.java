package com.performily.flowboard.payroll.domain.model.queries;

/**
 * Get Payslips By Employee Id Query
 * @summary
 * Query to get the PUBLISHED payslips of an employee for a year (the year of the pay period).
 *
 * @since 1.0.0
 */
public record GetPayslipsByEmployeeIdQuery(Long employeeId, Integer year) {
    /**
     * Compact constructor for GetPayslipsByEmployeeIdQuery.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public GetPayslipsByEmployeeIdQuery {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
        if (year == null) {
            throw new IllegalArgumentException("year is required");
        }
    }
}
