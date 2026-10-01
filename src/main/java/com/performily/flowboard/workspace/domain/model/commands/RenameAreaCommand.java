package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Rename Area Command
 * @summary
 * Command to rename an area.
 *
 * @since 1.0.0
 */
public record RenameAreaCommand(Long areaId, String name) {
    /**
     * Compact constructor for RenameAreaCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public RenameAreaCommand {
        if (areaId == null || areaId <= 0) {
            throw new IllegalArgumentException("areaId cannot be null or less than 1");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or blank");
        }
    }
}