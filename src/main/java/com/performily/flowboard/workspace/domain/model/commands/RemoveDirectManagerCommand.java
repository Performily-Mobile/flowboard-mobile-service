package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Remove Direct Manager Command
 * @summary
 * Command to remove the direct manager of an employee.
 *
 * @since 1.0.0
 */
public record RemoveDirectManagerCommand(Long employeeId) {
    /**
     * Compact constructor for RemoveDirectManagerCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public RemoveDirectManagerCommand {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
    }
}