package com.performily.flowboard.payroll.domain.model.queries;

/**
 * Get Payslip Download Url Query
 * @summary
 * Query to get a temporary download link for a payslip of the employee that asks for it.
 *
 * @since 1.0.0
 */
public record GetPayslipDownloadUrlQuery(Long payslipId, Long requesterId) {
    /**
     * Compact constructor for GetPayslipDownloadUrlQuery.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public GetPayslipDownloadUrlQuery {
        if (payslipId == null || payslipId <= 0) {
            throw new IllegalArgumentException("payslipId cannot be null or less than 1");
        }
        if (requesterId == null || requesterId <= 0) {
            throw new IllegalArgumentException("requesterId cannot be null or less than 1");
        }
    }
}
