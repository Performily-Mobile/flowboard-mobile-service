package com.performily.flowboard.workspace.domain.model.queries;

/**
 * Get Active Employee Count By Area Id Query
 * @summary
 * Query to count the ACTIVE employees of an area.
 *
 * @param areaId the identifier. It cannot be null or less than 1.
 * @since 1.0.0
 */
public record GetActiveEmployeeCountByAreaIdQuery(Long areaId) {
    /**
     * Compact constructor for GetActiveEmployeeCountByAreaIdQuery.
     *
     * @throws IllegalArgumentException if the identifier is null or less than 1
     */
    public GetActiveEmployeeCountByAreaIdQuery {
        if (areaId == null || areaId <= 0) {
            throw new IllegalArgumentException("areaId cannot be null or less than 1");
        }
    }
}