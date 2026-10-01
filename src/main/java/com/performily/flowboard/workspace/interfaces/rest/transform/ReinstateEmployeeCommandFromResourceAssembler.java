package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.ReinstateEmployeeCommand;
import com.performily.flowboard.workspace.interfaces.rest.resources.ReinstateEmployeeResource;

/**
 * Reinstate Employee Command From Resource Assembler
 * @summary
 * Assembler to convert a ReinstateEmployeeResource to a ReinstateEmployeeCommand.
 *
 * @since 1.0.0
 */
public class ReinstateEmployeeCommandFromResourceAssembler {
    /**
     * Converts a {@link ReinstateEmployeeResource} to a {@link ReinstateEmployeeCommand}.
     *
     * @param employeeId the employee id
     * @param resource the {@link ReinstateEmployeeResource} instance
     * @return the {@link ReinstateEmployeeCommand}
     */
    public static ReinstateEmployeeCommand toCommandFromResource(Long employeeId, ReinstateEmployeeResource resource) {
        return new ReinstateEmployeeCommand(employeeId, resource.areaId(), resource.positionId(), resource.reinstatementDate());
    }
}