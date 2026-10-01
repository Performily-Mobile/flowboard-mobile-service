package com.performily.flowboard.workspace.domain.model.events;

import java.time.LocalDate;

/**
 * Employee Reinstated Event
 * @summary
 * Domain event registered when a terminated employee is reinstated.
 *
 * @param employeeId        the employee id
 * @param reinstatementDate the reinstatement date
 * @since 1.0.0
 */
public record EmployeeReinstatedEvent(Long employeeId, LocalDate reinstatementDate) {
}