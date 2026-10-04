package com.performily.flowboard.request.domain.model.queries;

/**
 * Get Pending Requests For Hr Staff Query
 * @summary
 * Query to get the IN_PROGRESS requests routed to the HR staff, oldest first. The request type filter is optional.
 *
 * @since 1.0.0
 */
public record GetPendingRequestsForHrStaffQuery(Long requestTypeId) {
}
