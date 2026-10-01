package com.performily.flowboard.workspace.domain.model.commands;

import java.time.LocalDate;

/**
 * Reinstate Employee Command
 * @summary
 * Command to reinstate a TERMINATED employee.
 *
 * @since 1.0.0
 */
public record ReinstateEmployeeCommand(
        Long employeeId,
        Long areaId,
        Long positionId,
        LocalDate reinstatementDate) {
    /**
     * Compact constructor for ReinstateEmployeeCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public ReinstateEmployeeCommand {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
        if (areaId == null || areaId <= 0) {
            throw new IllegalArgumentException("areaId cannot be null or less than 1");
        }
        if (positionId == null || positionId <= 0) {
            throw new IllegalArgumentException("positionId cannot be null or less than 1");
        }
        if (reinstatementDate == null) {
            throw new IllegalArgumentException("reinstatementDate cannot be null");
        }
    }
}