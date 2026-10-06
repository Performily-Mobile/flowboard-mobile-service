package com.performily.flowboard.payroll.domain.model.events;

import java.time.LocalDateTime;

/**
 * Payslip Downloaded Event
 * @summary
 * Domain event published when an employee downloads a payslip (audit trail for the personal data law).
 *
 * @param payslipId the payslip id
 * @param employeeId the employee id
 * @param occurredOn when it happened
 * @since 1.0.0
 */
public record PayslipDownloadedEvent(Long payslipId, Long employeeId, LocalDateTime occurredOn) {
}
