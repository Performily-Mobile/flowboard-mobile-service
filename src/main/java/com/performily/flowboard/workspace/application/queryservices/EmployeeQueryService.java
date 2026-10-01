package com.performily.flowboard.workspace.application.queryservices;

import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import com.performily.flowboard.workspace.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * Employee Query Service
 * @summary
 * Application service contract for employee read queries.
 *
 * @since 1.0.0
 */
public interface EmployeeQueryService {
    /**
     * Handles retrieval of an employee by id.
     *
     * @param query employee-id query
     * @return matching employee, if found
     */
    Optional<Employee> handle(GetEmployeeByIdQuery query);

    /**
     * Handles the employee search with optional filters.
     *
     * @param query search query with the optional filters
     * @return list of employees ordered by last name
     */
    List<Employee> handle(SearchEmployeesQuery query);

    /**
     * Handles the count of ACTIVE employees of an area.
     *
     * @param query area-id query
     * @return number of active employees of the area
     */
    long handle(GetActiveEmployeeCountByAreaIdQuery query);

    /**
     * Handles retrieval of the direct subordinates of an employee.
     *
     * @param query manager-id query
     * @return list of direct subordinates
     */
    List<Employee> handle(GetAllEmployeesByDirectManagerIdQuery query);

    /**
     * Handles retrieval of the employees that make up the organization chart.
     *
     * @param query query marker
     * @return list of employees that are not TERMINATED
     */
    List<Employee> handle(GetOrganizationChartQuery query);
}