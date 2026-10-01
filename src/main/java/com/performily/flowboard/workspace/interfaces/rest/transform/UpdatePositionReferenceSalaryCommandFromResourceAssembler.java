package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.UpdatePositionReferenceSalaryCommand;
import com.performily.flowboard.workspace.interfaces.rest.resources.UpdatePositionReferenceSalaryResource;

/**
 * Update Position Reference Salary Command From Resource Assembler
 * @summary
 * Assembler to convert an UpdatePositionReferenceSalaryResource to an UpdatePositionReferenceSalaryCommand.
 *
 * @since 1.0.0
 */
public class UpdatePositionReferenceSalaryCommandFromResourceAssembler {
    /**
     * Converts a {@link UpdatePositionReferenceSalaryResource} to a {@link UpdatePositionReferenceSalaryCommand}.
     *
     * @param positionId the position id
     * @param resource the {@link UpdatePositionReferenceSalaryResource} instance
     * @return the {@link UpdatePositionReferenceSalaryCommand}
     */
    public static UpdatePositionReferenceSalaryCommand toCommandFromResource(Long positionId,
                                                                             UpdatePositionReferenceSalaryResource resource) {
        return new UpdatePositionReferenceSalaryCommand(
                positionId,
                resource.referenceSalaryAmount(),
                resource.referenceSalaryCurrency());
    }
}