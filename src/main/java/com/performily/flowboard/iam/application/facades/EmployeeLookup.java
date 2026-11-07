package com.performily.flowboard.iam.application.facades;

import com.performily.flowboard.workspace.application.facades.WorkspaceContextFacade;
import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import org.springframework.stereotype.Component;

/**
 * Anti-corruption access from IAM to the Workspace bounded context.
 * IAM only needs to know whether an employee can sign in, so it asks the Workspace
 * facade instead of reading the employees table.
 */
@Component
public class EmployeeLookup {

    private final WorkspaceContextFacade workspace;

    public EmployeeLookup(WorkspaceContextFacade workspace) {
        this.workspace = workspace;
    }

    public boolean existsActive(Long id) {
        return workspace.findEmployeeById(id)
                .map(Employee::isActive)
                .orElse(false);
    }
}
