package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.AssignJobCommand;
import com.performily.flowboard.workspace.interfaces.rest.resources.AssignJobResource;

/**
 * Assign Job Command From Resource Assembler
 * @summary
 * Assembler to convert an AssignJobResource to an AssignJobCommand.
 *
 * @since 1.0.0
 */
public class AssignJobCommandFromResourceAssembler {
    /**
     * Converts a {@link AssignJobResource} to a {@link AssignJobCommand}.
     *
     * @param employeeId the employee id
     * @param resource the {@link AssignJobResource} instance
     * @return the {@link AssignJobCommand}
     */
    public static AssignJobCommand toCommandFromResource(Long employeeId, AssignJobResource resource) {
        return new AssignJobCommand(employeeId, resource.areaId(), resource.positionId(), resource.effectiveDate());
    }
}