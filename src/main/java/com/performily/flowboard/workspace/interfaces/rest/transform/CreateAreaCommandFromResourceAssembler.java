package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.CreateAreaCommand;
import com.performily.flowboard.workspace.interfaces.rest.resources.CreateAreaResource;

/**
 * Create Area Command From Resource Assembler
 * @summary
 * Assembler to convert a CreateAreaResource to a CreateAreaCommand.
 *
 * @since 1.0.0
 */
public class CreateAreaCommandFromResourceAssembler {
    /**
     * Converts a {@link CreateAreaResource} to a {@link CreateAreaCommand}.
     *
     * @param resource the {@link CreateAreaResource} instance
     * @return the {@link CreateAreaCommand}
     */
    public static CreateAreaCommand toCommandFromResource(CreateAreaResource resource) {
        return new CreateAreaCommand(resource.name(), resource.description());
    }
}