package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.CreatePositionCommand;
import com.performily.flowboard.workspace.interfaces.rest.resources.CreatePositionResource;

/**
 * Create Position Command From Resource Assembler
 * @summary
 * Assembler to convert a CreatePositionResource to a CreatePositionCommand.
 *
 * @since 1.0.0
 */
public class CreatePositionCommandFromResourceAssembler {
    /**
     * Converts a {@link CreatePositionResource} to a {@link CreatePositionCommand}.
     *
     * @param resource the {@link CreatePositionResource} instance
     * @return the {@link CreatePositionCommand}
     */
    public static CreatePositionCommand toCommandFromResource(CreatePositionResource resource) {
        return new CreatePositionCommand(
                resource.title(),
                resource.areaId(),
                resource.referenceSalaryAmount(),
                resource.referenceSalaryCurrency());
    }
}