package com.performily.flowboard.workspace.infrastructure.persistence.jpa.assemblers;

import com.performily.flowboard.shared.domain.model.valueobjects.Money;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.embeddables.MoneyPersistenceEmbeddable;
import com.performily.flowboard.workspace.domain.model.entities.Position;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities.PositionPersistenceEntity;

import java.util.Currency;

/**
 * Position Persistence Assembler
 * @summary
 * Static assembler between position domain and persistence representations.
 *
 * @since 1.0.0
 */
public final class PositionPersistenceAssembler {
    private PositionPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity to its domain representation.
     *
     * @param entity the {@link PositionPersistenceEntity} instance
     * @return the domain object, or null when the entity is null
     */
    public static Position toDomainFromPersistence(PositionPersistenceEntity entity) {
        if (entity == null) return null;
        var salary = entity.getReferenceSalary();
        return new Position(
                entity.getId(),
                entity.getTitle(),
                AreaPersistenceAssembler.toDomainFromPersistence(entity.getArea()),
                new Money(salary.getAmount(), Currency.getInstance(salary.getCurrency())),
                entity.isActive());
    }

    /**
     * Converts a domain object to its persistence representation.
     *
     * @param position the {@link Position} instance
     * @return the persistence entity, or null when the domain object is null
     */
    public static PositionPersistenceEntity toPersistenceFromDomain(Position position) {
        if (position == null) return null;
        var entity = new PositionPersistenceEntity();
        if (position.getId() != null) {
            entity.setId(position.getId());
        }
        entity.setTitle(position.getTitle());
        entity.setArea(AreaPersistenceAssembler.toPersistenceFromDomain(position.getArea()));
        entity.setReferenceSalary(new MoneyPersistenceEmbeddable(
                position.getReferenceSalary().amount(),
                position.getReferenceSalary().currency().getCurrencyCode()));
        entity.setActive(position.isActive());
        return entity;
    }
}