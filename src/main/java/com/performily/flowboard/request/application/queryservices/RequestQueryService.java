package com.performily.flowboard.request.application.queryservices;

import com.performily.flowboard.request.domain.model.aggregates.Request;
import com.performily.flowboard.request.domain.model.queries.GetPendingRequestsByApproverIdQuery;
import com.performily.flowboard.request.domain.model.queries.GetPendingRequestsForHrStaffQuery;
import com.performily.flowboard.request.domain.model.queries.GetRequestByIdQuery;
import com.performily.flowboard.request.domain.model.queries.GetRequestsByRequesterIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Request Query Service
 * @summary
 * Application service contract for request read queries.
 *
 * @since 1.0.0
 */
public interface RequestQueryService {
    /**
     * Handles retrieval of a request by id.
     *
     * @param query request-id query
     * @return matching request, if found
     */
    Optional<Request> handle(GetRequestByIdQuery query);

    /**
     * Handles retrieval of the requests of an employee.
     *
     * @param query requester-id query
     * @return list of requests
     */
    List<Request> handle(GetRequestsByRequesterIdQuery query);

    /**
     * Handles retrieval of the pending requests of a direct manager.
     *
     * @param query approver-id query
     * @return list of requests
     */
    List<Request> handle(GetPendingRequestsByApproverIdQuery query);

    /**
     * Handles retrieval of the pending requests routed to the HR staff.
     *
     * @param query hr-staff query
     * @return list of requests
     */
    List<Request> handle(GetPendingRequestsForHrStaffQuery query);
}
