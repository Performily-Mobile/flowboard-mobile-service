package com.performily.flowboard.request.domain.model.queries;

import com.performily.flowboard.shared.domain.model.valueobjects.PageCriteria;

/**
 * Get Pending Requests For Hr Staff Query
 * @summary
 * Query to get one page of the IN_PROGRESS requests routed to the HR staff, oldest first. The request type filter is optional.
 *
 * @since 1.0.0
 */
public record GetPendingRequestsForHrStaffQuery(Long requestTypeId, PageCriteria page) {
    /**
     * Compact constructor for GetPendingRequestsForHrStaffQuery. Uses the default page when none is given.
     */
    public GetPendingRequestsForHrStaffQuery {
        if (page == null) {
            page = PageCriteria.of(null, null);
        }
    }
}
