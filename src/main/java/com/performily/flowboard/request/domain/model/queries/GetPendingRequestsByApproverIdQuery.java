package com.performily.flowboard.request.domain.model.queries;

/**
 * Get Pending Requests By Approver Id Query
 * @summary
 * Query to get the IN_PROGRESS requests assigned to a direct manager, oldest first. The request type filter is optional.
 *
 * @since 1.0.0
 */
public record GetPendingRequestsByApproverIdQuery(Long approverId, Long requestTypeId) {
    /**
     * Compact constructor for GetPendingRequestsByApproverIdQuery.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public GetPendingRequestsByApproverIdQuery {
        if (approverId == null || approverId <= 0) {
            throw new IllegalArgumentException("approverId cannot be null or less than 1");
        }
    }
}
