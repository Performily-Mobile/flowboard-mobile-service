package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.entities.Area;
import com.performily.flowboard.workspace.interfaces.rest.resources.AreaResource;

/**
 * Area Resource From Entity Assembler
 * @summary
 * Assembler to convert an Area entity to an AreaResource.
 *
 * @since 1.0.0
 */
public class AreaResourceFromEntityAssembler {
    /**
     * Converts a {@link Area} to a {@link AreaResource}.
     *
     * @param entity the {@link Area} instance
     * @return the {@link AreaResource}
     */
    public static AreaResource toResourceFromEntity(Area entity) {
        return new AreaResource(entity.getId(), entity.getName(), entity.getDescription(), entity.isActive());
    }
}