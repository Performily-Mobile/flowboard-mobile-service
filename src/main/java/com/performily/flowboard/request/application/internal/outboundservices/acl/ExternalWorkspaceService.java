package com.performily.flowboard.request.application.internal.outboundservices.acl;

import com.performily.flowboard.workspace.application.facades.WorkspaceContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * External Workspace Service
 * @summary
 * Anti-corruption layer between Request and Workspace. Request only needs to know
 * whether the requester exists, whether it is ACTIVE and who its direct manager is,
 * so this service translates the Workspace Employee into that small view and the
 * rest of the context does not depend on the Workspace model.
 *
 * @since 1.0.0
 */
@Service
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
     * Gets the data of an employee that Request needs.
     *
     * @param employeeId the employee id
     * @return the employee view, if the employee exists
     */
    public Optional<WorkspaceEmployee> fetchEmployeeById(Long employeeId) {
        return workspaceContextFacade.findEmployeeById(employeeId)
                .map(employee -> new WorkspaceEmployee(
                        employee.getId(),
                        employee.isActive(),
                        employee.getDirectManagerId() == null ? null : employee.getDirectManagerId().value()));
    }

    /**
     * View of a Workspace employee used by the Request context.
     *
     * @param id              the employee id
     * @param active          whether the employee is ACTIVE
     * @param directManagerId the direct manager id, or null when it has none
     */
    public record WorkspaceEmployee(Long id, boolean active, Long directManagerId) {
        /**
         * Checks whether the employee has a direct manager.
         *
         * @return true if it has
         */
        public boolean hasDirectManager() {
            return directManagerId != null;
        }
    }
}
