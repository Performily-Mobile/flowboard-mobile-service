package com.performily.flowboard.workspace.domain.model.events;

import com.performily.flowboard.workspace.domain.model.aggregates.Employee;

/**
 * Employee Registered Event
 * @summary
 * Domain event registered when a new {@link Employee} has been registered and persisted.
 *
 * @param employeeId the identity assigned to the new employee
 * @param firstName  the employee first name
 * @param lastName   the employee last name
 * @param email      the employee e-mail address
 * @param areaId     the assigned area id
 * @param positionId the assigned position id
 * @since 1.0.0
 */
public record EmployeeRegisteredEvent(
        Long employeeId,
        String firstName,
        String lastName,
        String email,
        Long areaId,
        Long positionId) {
    /**
     * Builds the event from a saved {@link Employee}.
     *
     * @param employee the saved employee (must already carry a non-null id)
     * @return the event
     */
    public static EmployeeRegisteredEvent from(Employee employee) {
        return new EmployeeRegisteredEvent(
                employee.getId(),
                employee.getName().firstName(),
                employee.getName().lastName(),
                employee.getContactInfo().email().value(),
                employee.getArea().getId(),
                employee.getPosition().getId());
    }
}