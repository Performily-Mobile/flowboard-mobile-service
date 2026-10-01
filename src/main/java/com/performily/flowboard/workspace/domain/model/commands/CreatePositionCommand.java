package com.performily.flowboard.workspace.domain.model.commands;

import java.math.BigDecimal;

/**
 * Create Position Command
 * @summary
 * Command to create a position inside an area. The currency is optional (default PEN).
 *
 * @since 1.0.0
 */
public record CreatePositionCommand(
        String title,
        Long areaId,
        BigDecimal referenceSalaryAmount,
        String referenceSalaryCurrency) {
    /**
     * Compact constructor for CreatePositionCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public CreatePositionCommand {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title cannot be null or blank");
        }
        if (areaId == null || areaId <= 0) {
            throw new IllegalArgumentException("areaId cannot be null or less than 1");
        }
        if (referenceSalaryAmount == null) {
            throw new IllegalArgumentException("referenceSalaryAmount cannot be null");
        }
    }
}