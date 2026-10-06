package com.performily.flowboard.payroll.domain.model.events;

/**
 * Payslip Published Event
 * @summary
 * Domain event registered when a payslip is published.
 *
 * @param payslipId the payslip id
 * @param employeeId the employee id
 * @param periodYear the period year
 * @param periodMonth the period month
 * @since 1.0.0
 */
public record PayslipPublishedEvent(Long payslipId, Long employeeId, int periodYear, int periodMonth) {
}
