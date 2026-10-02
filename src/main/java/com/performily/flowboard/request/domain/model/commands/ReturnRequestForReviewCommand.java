package com.performily.flowboard.request.domain.model.commands;

/**
 * Return Request For Review Command
 * @summary
 * Command to return a request to the requester asking for more information. The comment is required.
 *
 * @since 1.0.0
 */
public record ReturnRequestForReviewCommand(Long requestId, Long actorId, String comment) {
    /**
     * Compact constructor for ReturnRequestForReviewCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public ReturnRequestForReviewCommand {
        if (requestId == null || requestId <= 0) {
            throw new IllegalArgumentException("requestId cannot be null or less than 1");
        }
        if (actorId == null || actorId <= 0) {
            throw new IllegalArgumentException("actorId cannot be null or less than 1");
        }
    }
}
