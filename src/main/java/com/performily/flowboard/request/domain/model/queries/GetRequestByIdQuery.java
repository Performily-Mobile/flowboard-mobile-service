package com.performily.flowboard.request.domain.model.queries;

/**
 * Get Request By Id Query
 * @summary
 * Query to get a request with its history by id.
 *
 * @since 1.0.0
 */
public record GetRequestByIdQuery(Long requestId) {
    /**
     * Compact constructor for GetRequestByIdQuery.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public GetRequestByIdQuery {
        if (requestId == null || requestId <= 0) {
            throw new IllegalArgumentException("requestId cannot be null or less than 1");
        }
    }
}
