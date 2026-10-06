package com.performily.flowboard.payroll.domain.model.events;

/**
 * Payment Observed Event
 * @summary
 * Domain event registered when the payment of a payslip is observed.
 *
 * @param payslipId the payslip id
 * @param employeeId the employee id
 * @param reason the observation reason
 * @since 1.0.0
 */
public record PaymentObservedEvent(Long payslipId, Long employeeId, String reason) {
}
