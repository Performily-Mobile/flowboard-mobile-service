package com.performily.flowboard.request.domain.model.queries;

/**
 * Get Request Type By Id Query
 * @summary
 * Query to get a request type by id.
 *
 * @since 1.0.0
 */
public record GetRequestTypeByIdQuery(Long requestTypeId) {
    /**
     * Compact constructor for GetRequestTypeByIdQuery.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public GetRequestTypeByIdQuery {
        if (requestTypeId == null || requestTypeId <= 0) {
            throw new IllegalArgumentException("requestTypeId cannot be null or less than 1");
        }
    }
}
