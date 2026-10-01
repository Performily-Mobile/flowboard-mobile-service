package com.performily.flowboard.workspace.application.queryservices;

import com.performily.flowboard.workspace.domain.model.entities.Area;
import com.performily.flowboard.workspace.domain.model.queries.GetAllAreasQuery;
import com.performily.flowboard.workspace.domain.model.queries.GetAreaByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Area Query Service
 * @summary
 * Application service contract for area read queries.
 *
 * @since 1.0.0
 */
public interface AreaQueryService {
    /**
     * Handles retrieval of an area by id.
     *
     * @param query area-id query
     * @return matching area, if found
     */
    Optional<Area> handle(GetAreaByIdQuery query);

    /**
     * Handles retrieval of all areas.
     *
     * @param query query marker
     * @return list of areas
     */
    List<Area> handle(GetAllAreasQuery query);
}