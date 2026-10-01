package com.performily.flowboard.workspace.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities.AreaPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Area Persistence Repository
 * @summary
 * Spring Data repository for area persistence entities.
 *
 * @since 1.0.0
 */
@Repository
public interface AreaPersistenceRepository extends JpaRepository<AreaPersistenceEntity, Long> {
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