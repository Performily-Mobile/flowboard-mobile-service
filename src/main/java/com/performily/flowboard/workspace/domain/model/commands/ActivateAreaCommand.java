package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Activate Area Command
 * @summary
 * Command to activate an area.
 *
 * @since 1.0.0
 */
public record ActivateAreaCommand(Long areaId) {
    /**
     * Compact constructor for ActivateAreaCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public ActivateAreaCommand {
        if (areaId == null || areaId <= 0) {
            throw new IllegalArgumentException("areaId cannot be null or less than 1");
        }
    }
}