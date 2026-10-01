package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Assign Direct Manager Command
 * @summary
 * Command to assign the direct manager of an employee.
 *
 * @since 1.0.0
 */
public record AssignDirectManagerCommand(Long employeeId, Long managerId) {
    /**
     * Compact constructor for AssignDirectManagerCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public AssignDirectManagerCommand {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
        if (managerId == null || managerId <= 0) {
            throw new IllegalArgumentException("managerId cannot be null or less than 1");
        }
    }
}