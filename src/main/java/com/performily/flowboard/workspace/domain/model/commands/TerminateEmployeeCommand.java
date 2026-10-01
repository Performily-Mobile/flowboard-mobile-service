package com.performily.flowboard.workspace.domain.model.commands;

import java.time.LocalDate;

/**
 * Terminate Employee Command
 * @summary
 * Command to terminate an employee.
 *
 * @since 1.0.0
 */
public record TerminateEmployeeCommand(Long employeeId, String reason, LocalDate terminationDate) {
    /**
     * Compact constructor for TerminateEmployeeCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public TerminateEmployeeCommand {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason cannot be null or blank");
        }
        if (terminationDate == null) {
            throw new IllegalArgumentException("terminationDate cannot be null");
        }
    }
}