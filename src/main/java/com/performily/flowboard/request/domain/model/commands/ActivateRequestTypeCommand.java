package com.performily.flowboard.request.domain.model.commands;

/**
 * Activate Request Type Command
 * @summary
 * Command to activate a request type.
 *
 * @since 1.0.0
 */
public record ActivateRequestTypeCommand(Long requestTypeId) {
    /**
     * Compact constructor for ActivateRequestTypeCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public ActivateRequestTypeCommand {
        if (requestTypeId == null || requestTypeId <= 0) {
            throw new IllegalArgumentException("requestTypeId cannot be null or less than 1");
        }
    }
}
