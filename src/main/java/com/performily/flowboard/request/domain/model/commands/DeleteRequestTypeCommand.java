package com.performily.flowboard.request.domain.model.commands;

/**
 * Delete Request Type Command
 * @summary
 * Command to delete a request type. A type with requests cannot be deleted; it must be deactivated.
 *
 * @since 1.0.0
 */
public record DeleteRequestTypeCommand(Long requestTypeId) {
    /**
     * Compact constructor for DeleteRequestTypeCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public DeleteRequestTypeCommand {
        if (requestTypeId == null || requestTypeId <= 0) {
            throw new IllegalArgumentException("requestTypeId cannot be null or less than 1");
        }
    }
}
