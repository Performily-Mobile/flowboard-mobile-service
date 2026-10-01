package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Suspend Employee Command
 * @summary
 * Command to suspend an ACTIVE employee.
 *
 * @since 1.0.0
 */
public record SuspendEmployeeCommand(Long employeeId) {
    /**
     * Compact constructor for SuspendEmployeeCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public SuspendEmployeeCommand {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
    }
}