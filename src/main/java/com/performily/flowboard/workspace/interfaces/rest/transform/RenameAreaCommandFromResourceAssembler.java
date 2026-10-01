package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.RenameAreaCommand;
import com.performily.flowboard.workspace.interfaces.rest.resources.RenameAreaResource;

/**
 * Rename Area Command From Resource Assembler
 * @summary
 * Assembler to convert a RenameAreaResource to a RenameAreaCommand.
 *
 * @since 1.0.0
 */
public class RenameAreaCommandFromResourceAssembler {
    /**
     * Converts a {@link RenameAreaResource} to a {@link RenameAreaCommand}.
     *
     * @param areaId the area id
     * @param resource the {@link RenameAreaResource} instance
     * @return the {@link RenameAreaCommand}
     */
    public static RenameAreaCommand toCommandFromResource(Long areaId, RenameAreaResource resource) {
        return new RenameAreaCommand(areaId, resource.name());
    }
}