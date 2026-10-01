package com.performily.flowboard.workspace.domain.model.queries;

/**
 * Get All Employees By Direct Manager Id Query
 * @summary
 * Query to get the direct subordinates of an employee.
 *
 * @param managerId the identifier. It cannot be null or less than 1.
 * @since 1.0.0
 */
public record GetAllEmployeesByDirectManagerIdQuery(Long managerId) {
    /**
     * Compact constructor for GetAllEmployeesByDirectManagerIdQuery.
     *
     * @throws IllegalArgumentException if the identifier is null or less than 1
     */
    public GetAllEmployeesByDirectManagerIdQuery {
        if (managerId == null || managerId <= 0) {
            throw new IllegalArgumentException("managerId cannot be null or less than 1");
        }
    }
}