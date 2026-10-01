package com.performily.flowboard.workspace.domain.model.queries;

import com.performily.flowboard.workspace.domain.model.valueobjects.EmploymentStatus;

/**
 * Search Employees Query
 * @summary
 * Query to get the employees ordered by last name. Every filter is optional:
 * search matches the first name, the last name or the identity document number.
 *
 * @param search     text to search, or null
 * @param areaId     area identifier, or null
 * @param status     employment status, or null
 * @param positionId position identifier, or null
 * @since 1.0.0
 */
public record SearchEmployeesQuery(String search, Long areaId, EmploymentStatus status, Long positionId) {
    /**
     * Compact constructor for SearchEmployeesQuery.
     *
     * @throws IllegalArgumentException if an identifier is less than 1
     */
    public SearchEmployeesQuery {
        if (search != null && search.isBlank()) {
            search = null;
        }
        if (areaId != null && areaId <= 0) {
            throw new IllegalArgumentException("areaId cannot be less than 1");
        }
        if (positionId != null && positionId <= 0) {
            throw new IllegalArgumentException("positionId cannot be less than 1");
        }
    }
}