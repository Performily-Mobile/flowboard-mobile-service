package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Deactivate Area Command
 * @summary
 * Command to deactivate an area. An area with ACTIVE employees cannot be deactivated.
 *
 * @since 1.0.0
 */
public record DeactivateAreaCommand(Long areaId) {
    /**
     * Compact constructor for DeactivateAreaCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public DeactivateAreaCommand {
        if (areaId == null || areaId <= 0) {
            throw new IllegalArgumentException("areaId cannot be null or less than 1");
        }
    }
}