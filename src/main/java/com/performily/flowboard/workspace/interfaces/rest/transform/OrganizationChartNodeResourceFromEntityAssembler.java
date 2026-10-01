package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import com.performily.flowboard.workspace.interfaces.rest.resources.OrganizationChartNodeResource;
import com.performily.flowboard.workspace.interfaces.rest.resources.OrganizationChartResource;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Organization Chart Node Resource From Entity Assembler
 * @summary
 * Assembler that builds the organization chart from the employees' directManagerId.
 *
 * An employee whose direct manager is no longer ACTIVE goes to the pending
 * reassignment list instead of the tree. The employees without a direct manager,
 * or whose manager belongs to another area, appear in the first level.
 *
 * @since 1.0.0
 */
public class OrganizationChartNodeResourceFromEntityAssembler {
    /**
     * Builds the organization chart.
     *
     * @param employees the employees that are not TERMINATED
     * @param areaId    the area to show, or null for the whole organization
     * @return the {@link OrganizationChartResource}
     */
    public static OrganizationChartResource toResourceFromEntities(List<Employee> employees, Long areaId) {
        var activeIds = employees.stream()
                .filter(Employee::isActive)
                .map(Employee::getId)
                .collect(Collectors.toSet());
        var scope = employees.stream()
                .filter(employee -> areaId == null || Objects.equals(employee.getArea().getId(), areaId))
                .toList();
        var scopeIds = scope.stream().map(Employee::getId).collect(Collectors.toSet());
        Map<Long, List<Employee>> subordinatesByManager = scope.stream()
                .filter(Employee::hasDirectManager)
                .filter(employee -> !isPendingReassignment(employee, activeIds))
                .collect(Collectors.groupingBy(employee -> employee.getDirectManagerId().value()));
        var visited = new HashSet<Long>();
        var pending = scope.stream()
                .filter(employee -> isPendingReassignment(employee, activeIds))
                .toList();
        var roots = scope.stream()
                .filter(employee -> !isPendingReassignment(employee, activeIds))
                .filter(employee -> !employee.hasDirectManager() || !scopeIds.contains(employee.getDirectManagerId().value()))
                .map(root -> toNode(root, subordinatesByManager, visited))
                .toList();
        var pendingNodes = pending.stream()
                .map(employee -> toNode(employee, subordinatesByManager, visited))
                .toList();
        return new OrganizationChartResource(roots, pendingNodes);
    }

    /**
     * Checks whether the direct manager of an employee is no longer ACTIVE.
     *
     * @param employee  the {@link Employee} instance
     * @param activeIds the ids of the ACTIVE employees
     * @return true when the employee needs a new direct manager
     */
    private static boolean isPendingReassignment(Employee employee, Set<Long> activeIds) {
        return employee.hasDirectManager() && !activeIds.contains(employee.getDirectManagerId().value());
    }

    /**
     * Builds the node of an employee with its subordinates.
     *
     * @param employee             the {@link Employee} instance
     * @param subordinatesByManager the subordinates grouped by direct manager id
     * @param visited              the ids already added to the chart
     * @return the {@link OrganizationChartNodeResource}
     */
    private static OrganizationChartNodeResource toNode(Employee employee,
                                                        Map<Long, List<Employee>> subordinatesByManager,
                                                        Set<Long> visited) {
        visited.add(employee.getId());
        var subordinates = subordinatesByManager.getOrDefault(employee.getId(), List.of()).stream()
                .filter(subordinate -> !visited.contains(subordinate.getId()))
                .map(subordinate -> toNode(subordinate, subordinatesByManager, visited))
                .toList();
        return new OrganizationChartNodeResource(
                employee.getId(),
                employee.getFullName(),
                employee.getPosition().getTitle(),
                employee.getArea().getName(),
                subordinates);
    }
}