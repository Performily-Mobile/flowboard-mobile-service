package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import com.performily.flowboard.workspace.interfaces.rest.resources.OrganizationChartNodeResource;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Organization Chart Node Resource From Entity Assembler
 * @summary
 * Assembler that builds the organization chart tree from the employees' directManagerId.
 *
 * @since 1.0.0
 */
public class OrganizationChartNodeResourceFromEntityAssembler {
    /**
     * Builds the chart. The roots are the employees without a direct manager
     * (or whose manager is not in the list).
     *
     * @param employees the employees that make up the chart
     * @return the root nodes, each one with its subordinates
     */
    public static List<OrganizationChartNodeResource> toResourcesFromEntities(List<Employee> employees) {
        var ids = employees.stream().map(Employee::getId).collect(Collectors.toSet());
        Map<Long, List<Employee>> subordinatesByManager = employees.stream()
                .filter(Employee::hasDirectManager)
                .collect(Collectors.groupingBy(employee -> employee.getDirectManagerId().value()));
        var visited = new HashSet<Long>();
        return employees.stream()
                .filter(employee -> !employee.hasDirectManager() || !ids.contains(employee.getDirectManagerId().value()))
                .map(root -> toNode(root, subordinatesByManager, visited))
                .toList();
    }

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