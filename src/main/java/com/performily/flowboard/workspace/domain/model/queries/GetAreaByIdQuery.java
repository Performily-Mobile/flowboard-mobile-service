package com.performily.flowboard.workspace.domain.model.queries;

/**
 * Get Area By Id Query
 * @summary
 * Query to get an area by id.
 *
 * @param areaId the identifier. It cannot be null or less than 1.
 * @since 1.0.0
 */
public record GetAreaByIdQuery(Long areaId) {
    /**
     * Compact constructor for GetAreaByIdQuery.
     *
     * @throws IllegalArgumentException if the identifier is null or less than 1
     */
    public GetAreaByIdQuery {
        if (areaId == null || areaId <= 0) {
            throw new IllegalArgumentException("areaId cannot be null or less than 1");
        }
    }
}