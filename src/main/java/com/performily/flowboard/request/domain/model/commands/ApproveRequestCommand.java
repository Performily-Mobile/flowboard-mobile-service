package com.performily.flowboard.request.domain.model.commands;

/**
 * Approve Request Command
 * @summary
 * Command to approve a request. actorId is the employee that approves it.
 *
 * @since 1.0.0
 */
public record ApproveRequestCommand(Long requestId, Long actorId) {
    /**
     * Compact constructor for ApproveRequestCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public ApproveRequestCommand {
        if (requestId == null || requestId <= 0) {
            throw new IllegalArgumentException("requestId cannot be null or less than 1");
        }
        if (actorId == null || actorId <= 0) {
            throw new IllegalArgumentException("actorId cannot be null or less than 1");
        }
    }
}
