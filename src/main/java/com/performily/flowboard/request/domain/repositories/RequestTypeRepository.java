package com.performily.flowboard.request.domain.repositories;

import com.performily.flowboard.request.domain.model.entities.RequestType;

import java.util.List;
import java.util.Optional;

/**
 * Request Type Repository
 * @summary
 * Request type repository port.
 *
 * @since 1.0.0
 */
public interface RequestTypeRepository {
    /**
     * Finds the request type with the given id, with its fields.
     *
     * @param id the id
     * @return the request type, if found
     */
    Optional<RequestType> findById(Long id);

    /**
     * Finds all the request types ordered by name.
     *
     * @return the list of request types
     */
    List<RequestType> findAll();

    /**
     * Finds the active request types ordered by name.
     *
     * @return the list of active request types
     */
    List<RequestType> findAllByActiveTrue();

    /**
     * Saves the request type with its fields.
     *
     * @param requestType the {@link RequestType} instance
     * @return the saved request type
     */
    RequestType save(RequestType requestType);

    /**
     * Deletes the request type with the given id.
     *
     * @param id the id
     */
    void deleteById(Long id);

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
