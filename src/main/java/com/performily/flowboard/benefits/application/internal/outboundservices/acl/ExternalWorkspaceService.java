package com.performily.flowboard.benefits.application.internal.outboundservices.acl;

import com.performily.flowboard.workspace.application.facades.WorkspaceContextFacade;
import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * External Workspace Service
 * @summary
 * Anti-corruption layer between Benefits and Workspace (Customer/Supplier).
 *
 * Benefits only needs to know if an employee exists and is ACTIVE, its name,
 * its hire date (for the vacation accrual) and its area. This class translates
 * the Workspace Employee aggregate into that small model, so a change in
 * Workspace only affects this class.
 *
 * The bean has its own name because Request already has a class with the same name.
 *
 * @since 1.0.0
 */
@Service("benefitsExternalWorkspaceService")
public class ExternalWorkspaceService {
    private final WorkspaceContextFacade workspaceContextFacade;

    public ExternalWorkspaceService(WorkspaceContextFacade workspaceContextFacade) {
        this.workspaceContextFacade = workspaceContextFacade;
    }

    public Optional<WorkspaceEmployee> fetchEmployeeById(Long employeeId) {
        if (employeeId == null) {
            return Optional.empty();
        }
        return workspaceContextFacade.findEmployeeById(employeeId).map(ExternalWorkspaceService::toWorkspaceEmployee);
    }

    public List<WorkspaceEmployee> fetchActiveEmployeesByAreaId(Long areaId) {
        return workspaceContextFacade.findActiveEmployeesByAreaId(areaId).stream()
                .map(ExternalWorkspaceService::toWorkspaceEmployee).toList();
    }

    public List<WorkspaceEmployee> fetchAllActiveEmployees() {
        return workspaceContextFacade.findAllActiveEmployees().stream()
                .map(ExternalWorkspaceService::toWorkspaceEmployee).toList();
    }

    private static WorkspaceEmployee toWorkspaceEmployee(Employee employee) {
        var area = employee.getArea();
        var period = employee.getEmploymentPeriod();
        return new WorkspaceEmployee(
                employee.getId(),
                employee.getFullName(),
                employee.isActive(),
                period == null ? null : period.hireDate(),
                area == null ? null : area.getId(),
                area == null ? null : area.getName());
    }

    /**
     * The view of an employee that Benefits needs.
     */
    public record WorkspaceEmployee(Long id, String fullName, boolean active, LocalDate hireDate,
                                    Long areaId, String areaName) {
    }
}
