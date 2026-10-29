package com.performily.flowboard.request.domain.model.queries;

import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;
import com.performily.flowboard.shared.domain.model.valueobjects.PageCriteria;

/**
 * Get Requests By Requester Id Query
 * @summary
 * Query to get one page of the requests of an employee, newest first. The status filter is optional.
 *
 * @since 1.0.0
 */
public record GetRequestsByRequesterIdQuery(Long requesterId, RequestStatus status, PageCriteria page) {
    /**
     * Compact constructor for GetRequestsByRequesterIdQuery.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public GetRequestsByRequesterIdQuery {
        if (requesterId == null || requesterId <= 0) {
            throw new IllegalArgumentException("requesterId cannot be null or less than 1");
        }
        if (page == null) {
            page = PageCriteria.of(null, null);
        }
    }
}
