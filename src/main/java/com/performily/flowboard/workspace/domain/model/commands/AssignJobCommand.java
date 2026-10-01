package com.performily.flowboard.workspace.domain.model.commands;

import java.time.LocalDate;

/**
 * Assign Job Command
 * @summary
 * Command to change the area and position of an employee.
 *
 * @since 1.0.0
 */
public record AssignJobCommand(
        Long employeeId,
        Long areaId,
        Long positionId,
        LocalDate effectiveDate) {
    /**
     * Compact constructor for AssignJobCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public AssignJobCommand {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
        if (areaId == null || areaId <= 0) {
            throw new IllegalArgumentException("areaId cannot be null or less than 1");
        }
        if (positionId == null || positionId <= 0) {
            throw new IllegalArgumentException("positionId cannot be null or less than 1");
        }
        if (effectiveDate == null) {
            throw new IllegalArgumentException("effectiveDate cannot be null");
        }
    }
}