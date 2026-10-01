package com.performily.flowboard.workspace.application.queryservices;

import com.performily.flowboard.workspace.domain.model.entities.Position;
import com.performily.flowboard.workspace.domain.model.queries.GetAllPositionsByAreaIdQuery;
import com.performily.flowboard.workspace.domain.model.queries.GetAllPositionsQuery;
import com.performily.flowboard.workspace.domain.model.queries.GetPositionByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Position Query Service
 * @summary
 * Application service contract for position read queries.
 *
 * @since 1.0.0
 */
public interface PositionQueryService {
    /**
     * Handles retrieval of a position by id.
     *
     * @param query position-id query
     * @return matching position, if found
     */
    Optional<Position> handle(GetPositionByIdQuery query);

    /**
     * Handles retrieval of all positions.
     *
     * @param query query marker
     * @return list of positions
     */
    List<Position> handle(GetAllPositionsQuery query);

    /**
     * Handles retrieval of the positions of an area.
     *
     * @param query area-id query
     * @return list of positions of the area
     */
    List<Position> handle(GetAllPositionsByAreaIdQuery query);
}