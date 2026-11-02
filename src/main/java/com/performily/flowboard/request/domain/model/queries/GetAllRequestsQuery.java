package com.performily.flowboard.request.domain.model.queries;

import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;
import com.performily.flowboard.shared.domain.model.valueobjects.PageCriteria;

import java.util.List;

/**
 * Get All Requests Query
 * @summary
 * Query to get one page of the requests of every employee, newest first. All the
 * filters are optional: requester, statuses (an empty list means all), request type
 * and direct manager that has to approve them.
 *
 * @since 1.0.0
 */
public record GetAllRequestsQuery(Long requesterId, List<RequestStatus> statuses, Long requestTypeId,
                                  Long approverId, PageCriteria page) {
    /**
     * Compact constructor for GetAllRequestsQuery. Uses the default page when none is given.
     */
    public GetAllRequestsQuery {
        statuses = statuses == null ? List.of() : List.copyOf(statuses);
        if (page == null) {
            page = PageCriteria.of(null, null);
        }
    }
}
