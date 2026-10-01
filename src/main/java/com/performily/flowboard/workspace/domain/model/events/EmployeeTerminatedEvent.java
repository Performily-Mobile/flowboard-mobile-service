package com.performily.flowboard.workspace.domain.model.events;

import java.time.LocalDate;

/**
 * Employee Terminated Event
 * @summary
 * Domain event registered when an employee is terminated.
 *
 * @param employeeId      the employee id
 * @param terminationDate the termination date
 * @since 1.0.0
 */
public record EmployeeTerminatedEvent(Long employeeId, LocalDate terminationDate) {
}