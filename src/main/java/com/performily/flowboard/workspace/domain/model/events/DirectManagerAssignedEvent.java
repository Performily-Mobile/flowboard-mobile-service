package com.performily.flowboard.workspace.domain.model.events;

/**
 * Direct Manager Assigned Event
 * @summary
 * Domain event registered when a direct manager is assigned to an employee.
 *
 * @param employeeId the employee id
 * @param managerId  the direct manager id
 * @since 1.0.0
 */
public record DirectManagerAssignedEvent(Long employeeId, Long managerId) {
}