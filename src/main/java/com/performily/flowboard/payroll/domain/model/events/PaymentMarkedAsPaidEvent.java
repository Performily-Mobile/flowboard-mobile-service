package com.performily.flowboard.payroll.domain.model.events;

import java.time.LocalDate;

/**
 * Payment Marked As Paid Event
 * @summary
 * Domain event registered when the payment of a payslip is marked as paid.
 *
 * @param payslipId the payslip id
 * @param employeeId the employee id
 * @param paidOn the payment date
 * @since 1.0.0
 */
public record PaymentMarkedAsPaidEvent(Long payslipId, Long employeeId, LocalDate paidOn) {
}
