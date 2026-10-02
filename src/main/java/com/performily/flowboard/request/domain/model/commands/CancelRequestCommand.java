package com.performily.flowboard.request.domain.model.commands;

/**
 * Cancel Request Command
 * @summary
 * Command to cancel a request. Only the requester can cancel it while it is not resolved.
 *
 * @since 1.0.0
 */
public record CancelRequestCommand(Long requestId, Long actorId) {
    /**
     * Compact constructor for CancelRequestCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public CancelRequestCommand {
        if (requestId == null || requestId <= 0) {
            throw new IllegalArgumentException("requestId cannot be null or less than 1");
        }
        if (actorId == null || actorId <= 0) {
            throw new IllegalArgumentException("actorId cannot be null or less than 1");
        }
    }
}
