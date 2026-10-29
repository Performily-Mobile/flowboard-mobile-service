package com.performily.flowboard.request.domain.model.queries;

import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;
import com.performily.flowboard.shared.domain.model.valueobjects.PageCriteria;

/**
 * Get All Requests Query
 * @summary
 * Query to get one page of the requests of every employee, newest first.
 * The requester, status and request type filters are optional.
 *
 * @since 1.0.0
 */
public record GetAllRequestsQuery(Long requesterId, RequestStatus status, Long requestTypeId, PageCriteria page) {
    /**
     * Compact constructor for GetAllRequestsQuery. Uses the default page when none is given.
     */
    public GetAllRequestsQuery {
        if (page == null) {
            page = PageCriteria.of(null, null);
        }
    }
}
