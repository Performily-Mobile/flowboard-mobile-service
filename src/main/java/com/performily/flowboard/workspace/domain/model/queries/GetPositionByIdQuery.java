package com.performily.flowboard.workspace.domain.model.queries;

/**
 * Get Position By Id Query
 * @summary
 * Query to get a position by id.
 *
 * @param positionId the identifier. It cannot be null or less than 1.
 * @since 1.0.0
 */
public record GetPositionByIdQuery(Long positionId) {
    /**
     * Compact constructor for GetPositionByIdQuery.
     *
     * @throws IllegalArgumentException if the identifier is null or less than 1
     */
    public GetPositionByIdQuery {
        if (positionId == null || positionId <= 0) {
            throw new IllegalArgumentException("positionId cannot be null or less than 1");
        }
    }
}