package com.performily.flowboard.request.domain.model.queries;

import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;

/**
 * Get Requests By Requester Id Query
 * @summary
 * Query to get the requests of an employee, newest first. The status filter is optional.
 *
 * @since 1.0.0
 */
public record GetRequestsByRequesterIdQuery(Long requesterId, RequestStatus status) {
    /**
     * Compact constructor for GetRequestsByRequesterIdQuery.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public GetRequestsByRequesterIdQuery {
        if (requesterId == null || requesterId <= 0) {
            throw new IllegalArgumentException("requesterId cannot be null or less than 1");
        }
    }
}
