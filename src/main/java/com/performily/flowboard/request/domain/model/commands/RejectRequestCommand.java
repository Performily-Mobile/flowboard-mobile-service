package com.performily.flowboard.request.domain.model.commands;

/**
 * Reject Request Command
 * @summary
 * Command to reject a request. The reason is required.
 *
 * @since 1.0.0
 */
public record RejectRequestCommand(Long requestId, Long actorId, String reason) {
    /**
     * Compact constructor for RejectRequestCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public RejectRequestCommand {
        if (requestId == null || requestId <= 0) {
            throw new IllegalArgumentException("requestId cannot be null or less than 1");
        }
        if (actorId == null || actorId <= 0) {
            throw new IllegalArgumentException("actorId cannot be null or less than 1");
        }
    }
}
