package com.performily.flowboard.payroll.application.internal.outboundservices.acl;

import com.performily.flowboard.workspace.application.facades.WorkspaceContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * External Workspace Service
 * @summary
 * Anti-corruption layer between Payroll and Workspace. Payroll only needs to know
 * whether an employee exists and its full name, so this class translates the
 * Workspace Employee into that small view and the rest of the context does not
 * depend on the Workspace model.
 *
 * The bean has its own name because Request and Benefits already have a class
 * with the same name.
 *
 * @since 1.0.0
 */
@Service("payrollExternalWorkspaceService")
public class ExternalWorkspaceService {
    private final WorkspaceContextFacade workspaceContextFacade;

    /**
     * Constructor.
     *
     * @param workspaceContextFacade the {@link WorkspaceContextFacade} instance
     */
    public ExternalWorkspaceService(WorkspaceContextFacade workspaceContextFacade) {
        this.workspaceContextFacade = workspaceContextFacade;
    }

    /**
     * Gets the data of an employee that Payroll needs.
     *
     * @param employeeId the employee id
     * @return the employee view, if the employee exists
     */
    public Optional<WorkspaceEmployee> fetchEmployeeById(Long employeeId) {
        if (employeeId == null) {
            return Optional.empty();
        }
        return workspaceContextFacade.findEmployeeById(employeeId)
                .map(employee -> new WorkspaceEmployee(employee.getId(), employee.getFullName()));
    }

    /**
     * Gets the full name of an employee.
     *
     * @param employeeId the employee id
     * @return the full name, or null when the employee does not exist
     */
    public String fetchEmployeeFullName(Long employeeId) {
        return fetchEmployeeById(employeeId).map(WorkspaceEmployee::fullName).orElse(null);
    }

    /**
     * View of a Workspace employee used by the Payroll context.
     *
     * @param id       the employee id
     * @param fullName the full name
     */
    public record WorkspaceEmployee(Long id, String fullName) {
    }
}
