package com.performily.flowboard.request.domain.model.commands;

/**
 * Deactivate Request Type Command
 * @summary
 * Command to deactivate a request type. Inactive types do not receive new requests.
 *
 * @since 1.0.0
 */
public record DeactivateRequestTypeCommand(Long requestTypeId) {
    /**
     * Compact constructor for DeactivateRequestTypeCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public DeactivateRequestTypeCommand {
        if (requestTypeId == null || requestTypeId <= 0) {
            throw new IllegalArgumentException("requestTypeId cannot be null or less than 1");
        }
    }
}
