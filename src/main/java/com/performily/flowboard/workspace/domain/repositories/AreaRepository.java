package com.performily.flowboard.workspace.domain.repositories;

import com.performily.flowboard.workspace.domain.model.entities.Area;

import java.util.List;
import java.util.Optional;

/**
 * Area Repository
 * @summary
 * Workspace area repository port.
 *
 * @since 1.0.0
 */
public interface AreaRepository {
    /**
     * Finds the area with the given id.
     *
     * @param id the id
     * @return the area, if found
     */
    Optional<Area> findById(Long id);

    /**
     * Finds all the areas.
     *
     * @return the list of areas
     */
    List<Area> findAll();

    /**
     * Saves the area.
     *
     * @param area the {@link Area} instance
     * @return the saved area
     */
    Area save(Area area);

    /**
     * Checks whether an area exists with the given id.
     *
     * @param id the id
     * @return true if it exists
     */
    boolean existsById(Long id);

    /**
     * Checks whether an area exists with the given name.
     *
     * @param name the name
     * @return true if it exists
     */
    boolean existsByName(String name);

    /**
     * Checks whether another area exists with the given name.
     *
     * @param name the name
     * @param id the id
     * @return true if it exists
     */
    boolean existsByNameAndIdIsNot(String name, Long id);
}