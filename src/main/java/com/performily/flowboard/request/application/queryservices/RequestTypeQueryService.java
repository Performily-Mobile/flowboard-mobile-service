package com.performily.flowboard.request.application.queryservices;

import com.performily.flowboard.request.domain.model.entities.RequestType;
import com.performily.flowboard.request.domain.model.queries.GetAllRequestTypesQuery;
import com.performily.flowboard.request.domain.model.queries.GetRequestTypeByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Request Type Query Service
 * @summary
 * Application service contract for request type read queries.
 *
 * @since 1.0.0
 */
public interface RequestTypeQueryService {
    /**
     * Handles retrieval of a request type by id.
     *
     * @param query request-type-id query
     * @return matching request type, if found
     */
    Optional<RequestType> handle(GetRequestTypeByIdQuery query);

    /**
     * Handles retrieval of the request types.
     *
     * @param query query with the active-only flag
     * @return list of request types
     */
    List<RequestType> handle(GetAllRequestTypesQuery query);
}
