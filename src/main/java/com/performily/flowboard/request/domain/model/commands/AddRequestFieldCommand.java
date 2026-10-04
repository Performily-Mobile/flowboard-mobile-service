package com.performily.flowboard.request.domain.model.commands;

import com.performily.flowboard.request.domain.model.valueobjects.FieldDataType;

/**
 * Add Request Field Command
 * @summary
 * Command to add a field to the form of a request type.
 *
 * @since 1.0.0
 */
public record AddRequestFieldCommand(Long requestTypeId, String key, String label, FieldDataType dataType, boolean required, int displayOrder) {
    /**
     * Compact constructor for AddRequestFieldCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public AddRequestFieldCommand {
        if (requestTypeId == null || requestTypeId <= 0) {
            throw new IllegalArgumentException("requestTypeId cannot be null or less than 1");
        }
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key cannot be null or blank");
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("label cannot be null or blank");
        }
        if (dataType == null) {
            throw new IllegalArgumentException("dataType cannot be null");
        }
    }
}
