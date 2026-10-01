package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.TerminateEmployeeCommand;
import com.performily.flowboard.workspace.interfaces.rest.resources.TerminateEmployeeResource;

/**
 * Terminate Employee Command From Resource Assembler
 * @summary
 * Assembler to convert a TerminateEmployeeResource to a TerminateEmployeeCommand.
 *
 * @since 1.0.0
 */
public class TerminateEmployeeCommandFromResourceAssembler {
    /**
     * Converts a {@link TerminateEmployeeResource} to a {@link TerminateEmployeeCommand}.
     *
     * @param employeeId the employee id
     * @param resource the {@link TerminateEmployeeResource} instance
     * @return the {@link TerminateEmployeeCommand}
     */
    public static TerminateEmployeeCommand toCommandFromResource(Long employeeId, TerminateEmployeeResource resource) {
        return new TerminateEmployeeCommand(employeeId, resource.reason(), resource.terminationDate());
    }
}