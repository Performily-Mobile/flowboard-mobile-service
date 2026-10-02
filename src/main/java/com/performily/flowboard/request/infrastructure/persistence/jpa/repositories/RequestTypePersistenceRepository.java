package com.performily.flowboard.request.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestTypePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Request Type Persistence Repository
 * @summary
 * Spring Data repository for request type persistence entities.
 *
 * @since 1.0.0
 */
@Repository
public interface RequestTypePersistenceRepository extends JpaRepository<RequestTypePersistenceEntity, Long> {
    /**
     * Finds all the request types ordered by name.
     *
     * @return the list of request types
     */
    List<RequestTypePersistenceEntity> findAllByOrderByNameAsc();

    /**
     * Finds the active request types ordered by name.
     *
     * @return the list of request types
     */
    List<RequestTypePersistenceEntity> findAllByActiveTrueOrderByNameAsc();

    /**
     * Checks whether a request type exists with the given name.
     *
     * @param name the name
     * @return true if it exists
     */
    boolean existsByName(String name);

    /**
     * Checks whether another request type exists with the given name.
     *
     * @param name the name
     * @param id   the id to exclude
     * @return true if it exists
     */
    boolean existsByNameAndIdIsNot(String name, Long id);
}
