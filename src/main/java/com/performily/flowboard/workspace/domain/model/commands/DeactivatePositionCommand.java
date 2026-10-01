package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Deactivate Position Command
 * @summary
 * Command to deactivate a position.
 *
 * @since 1.0.0
 */
public record DeactivatePositionCommand(Long positionId) {
    /**
     * Compact constructor for DeactivatePositionCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public DeactivatePositionCommand {
        if (positionId == null || positionId <= 0) {
            throw new IllegalArgumentException("positionId cannot be null or less than 1");
        }
    }
}