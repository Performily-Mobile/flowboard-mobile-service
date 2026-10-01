package com.performily.flowboard.workspace.domain.model.queries;

/**
 * Get Employee By Id Query
 * @summary
 * Query to get an employee by id.
 *
 * @param employeeId the identifier. It cannot be null or less than 1.
 * @since 1.0.0
 */
public record GetEmployeeByIdQuery(Long employeeId) {
    /**
     * Compact constructor for GetEmployeeByIdQuery.
     *
     * @throws IllegalArgumentException if the identifier is null or less than 1
     */
    public GetEmployeeByIdQuery {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
    }
}