package com.performily.flowboard.request.domain.model.commands;

/**
 * Remove Request Field Command
 * @summary
 * Command to remove a field from the form of a request type. Only allowed while the type has no requests.
 *
 * @since 1.0.0
 */
public record RemoveRequestFieldCommand(Long requestTypeId, Long fieldId) {
    /**
     * Compact constructor for RemoveRequestFieldCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public RemoveRequestFieldCommand {
        if (requestTypeId == null || requestTypeId <= 0) {
            throw new IllegalArgumentException("requestTypeId cannot be null or less than 1");
        }
        if (fieldId == null || fieldId <= 0) {
            throw new IllegalArgumentException("fieldId cannot be null or less than 1");
        }
    }
}
