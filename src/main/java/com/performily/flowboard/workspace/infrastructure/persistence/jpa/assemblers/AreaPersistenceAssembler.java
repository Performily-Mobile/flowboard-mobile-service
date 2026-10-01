package com.performily.flowboard.workspace.infrastructure.persistence.jpa.assemblers;

import com.performily.flowboard.workspace.domain.model.entities.Area;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities.AreaPersistenceEntity;

/**
 * Area Persistence Assembler
 * @summary
 * Static assembler between area domain and persistence representations.
 *
 * @since 1.0.0
 */
public final class AreaPersistenceAssembler {
    private AreaPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity to its domain representation.
     *
     * @param entity the {@link AreaPersistenceEntity} instance
     * @return the domain object, or null when the entity is null
     */
    public static Area toDomainFromPersistence(AreaPersistenceEntity entity) {
        if (entity == null) return null;
        return new Area(entity.getId(), entity.getName(), entity.getDescription(), entity.isActive());
    }

    /**
     * Converts a domain object to its persistence representation.
     *
     * @param area the {@link Area} instance
     * @return the persistence entity, or null when the domain object is null
     */
    public static AreaPersistenceEntity toPersistenceFromDomain(Area area) {
        if (area == null) return null;
        var entity = new AreaPersistenceEntity();
        if (area.getId() != null) {
            entity.setId(area.getId());
        }
        entity.setName(area.getName());
        entity.setDescription(area.getDescription());
        entity.setActive(area.isActive());
        return entity;
    }
}