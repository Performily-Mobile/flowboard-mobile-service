package com.performily.flowboard.workspace.domain.model.commands;

import java.math.BigDecimal;

/**
 * Update Position Reference Salary Command
 * @summary
 * Command to update the reference salary of a position. The currency is optional (default PEN).
 *
 * @since 1.0.0
 */
public record UpdatePositionReferenceSalaryCommand(Long positionId, BigDecimal referenceSalaryAmount, String referenceSalaryCurrency) {
    /**
     * Compact constructor for UpdatePositionReferenceSalaryCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public UpdatePositionReferenceSalaryCommand {
        if (positionId == null || positionId <= 0) {
            throw new IllegalArgumentException("positionId cannot be null or less than 1");
        }
        if (referenceSalaryAmount == null) {
            throw new IllegalArgumentException("referenceSalaryAmount cannot be null");
        }
    }
}