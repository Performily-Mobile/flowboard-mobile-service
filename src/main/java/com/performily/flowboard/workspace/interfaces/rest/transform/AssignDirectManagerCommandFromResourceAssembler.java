package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.AssignDirectManagerCommand;
import com.performily.flowboard.workspace.interfaces.rest.resources.AssignDirectManagerResource;

/**
 * Assign Direct Manager Command From Resource Assembler
 * @summary
 * Assembler to convert an AssignDirectManagerResource to an AssignDirectManagerCommand.
 *
 * @since 1.0.0
 */
public class AssignDirectManagerCommandFromResourceAssembler {
    /**
     * Converts a {@link AssignDirectManagerResource} to a {@link AssignDirectManagerCommand}.
     *
     * @param employeeId the employee id
     * @param resource the {@link AssignDirectManagerResource} instance
     * @return the {@link AssignDirectManagerCommand}
     */
    public static AssignDirectManagerCommand toCommandFromResource(Long employeeId, AssignDirectManagerResource resource) {
        return new AssignDirectManagerCommand(employeeId, resource.managerId());
    }
}