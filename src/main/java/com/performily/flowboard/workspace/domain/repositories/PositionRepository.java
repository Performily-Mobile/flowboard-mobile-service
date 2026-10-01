package com.performily.flowboard.workspace.domain.repositories;

import com.performily.flowboard.workspace.domain.model.entities.Position;

import java.util.List;
import java.util.Optional;

/**
 * Position Repository
 * @summary
 * Workspace position repository port.
 *
 * @since 1.0.0
 */
public interface PositionRepository {
    /**
     * Finds the position with the given id.
     *
     * @param id the id
     * @return the position, if found
     */
    Optional<Position> findById(Long id);

    /**
     * Finds all the positions.
     *
     * @return the list of positions
     */
    List<Position> findAll();

    /**
     * Finds the positions of an area.
     *
     * @param areaId the area id
     * @return the list of positions
     */
    List<Position> findAllByAreaId(Long areaId);

    /**
     * Saves the position.
     *
     * @param position the {@link Position} instance
     * @return the saved position
     */
    Position save(Position position);

    /**
     * Checks whether a position exists with the given id.
     *
     * @param id the id
     * @return true if it exists
     */
    boolean existsById(Long id);
}