package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Create Area Command
 * @summary
 * Command to create an area.
 *
 * @since 1.0.0
 */
public record CreateAreaCommand(String name, String description) {
    /**
     * Compact constructor for CreateAreaCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public CreateAreaCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or blank");
        }
    }
}