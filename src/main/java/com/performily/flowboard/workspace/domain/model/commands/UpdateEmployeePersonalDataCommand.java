package com.performily.flowboard.workspace.domain.model.commands;

import java.time.LocalDate;

/**
 * Update Employee Personal Data Command
 * @summary
 * Command to update the personal data of an employee.
 *
 * @since 1.0.0
 */
public record UpdateEmployeePersonalDataCommand(
        Long employeeId,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String email,
        String phoneNumber,
        String street,
        String district,
        String province,
        String department) {
    /**
     * Compact constructor for UpdateEmployeePersonalDataCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public UpdateEmployeePersonalDataCommand {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
    }
}