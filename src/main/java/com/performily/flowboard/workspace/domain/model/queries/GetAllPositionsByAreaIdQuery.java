package com.performily.flowboard.workspace.domain.model.queries;

/**
 * Get All Positions By Area Id Query
 * @summary
 * Query to get all positions of an area.
 *
 * @param areaId the identifier. It cannot be null or less than 1.
 * @since 1.0.0
 */
public record GetAllPositionsByAreaIdQuery(Long areaId) {
    /**
     * Compact constructor for GetAllPositionsByAreaIdQuery.
     *
     * @throws IllegalArgumentException if the identifier is null or less than 1
     */
    public GetAllPositionsByAreaIdQuery {
        if (areaId == null || areaId <= 0) {
            throw new IllegalArgumentException("areaId cannot be null or less than 1");
        }
    }
}