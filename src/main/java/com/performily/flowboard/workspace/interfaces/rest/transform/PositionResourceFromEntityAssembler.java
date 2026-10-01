package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.entities.Position;
import com.performily.flowboard.workspace.interfaces.rest.resources.PositionResource;

/**
 * Position Resource From Entity Assembler
 * @summary
 * Assembler to convert a Position entity to a PositionResource.
 *
 * @since 1.0.0
 */
public class PositionResourceFromEntityAssembler {
    /**
     * Converts a {@link Position} to a {@link PositionResource}.
     *
     * @param entity the {@link Position} instance
     * @return the {@link PositionResource}
     */
    public static PositionResource toResourceFromEntity(Position entity) {
        return new PositionResource(
                entity.getId(),
                entity.getTitle(),
                entity.getArea().getId(),
                entity.getArea().getName(),
                entity.getReferenceSalary().amount(),
                entity.getReferenceSalary().currency().getCurrencyCode(),
                entity.isActive());
    }
}