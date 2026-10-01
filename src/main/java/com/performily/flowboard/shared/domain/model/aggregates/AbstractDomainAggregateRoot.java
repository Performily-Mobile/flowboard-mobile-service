package com.performily.flowboard.shared.domain.model.aggregates;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Collection;

/**
 * Base class for all domain aggregate roots.
 *
 * <p>Extends Spring Data Commons' {@link AbstractAggregateRoot} to gain
 * domain event registration support ({@code registerEvent}) without
 * pulling in any JPA or persistence concern. Identity and auditing are
 * intentionally left to the infrastructure layer.</p>
 *
 * <p>All bounded-context domain aggregate roots should extend this class.
 * Persistence-specific behavior belongs to dedicated infrastructure
 * persistence entities.</p>
 *
 * @param <T> the concrete aggregate root type
 */
@NullMarked
public abstract class AbstractDomainAggregateRoot<T extends AbstractDomainAggregateRoot<T>>
        extends AbstractAggregateRoot<T> {

    /**
     * Registers a domain event to be published after this aggregate is saved.
     *
     * @param event the domain event to register
     */
    protected void registerDomainEvent(Object event) {
        super.registerEvent(event);
    }

    /**
     * Returns all domain events registered on this aggregate since the last publication.
     * Exposed as {@code public} so repository adapters can retrieve and publish them
     * after the aggregate has been persisted.
     *
     * @return the registered domain events
     */
    @Override
    public Collection<Object> domainEvents() {
        return super.domainEvents();
    }

    /**
     * Clears all registered domain events.
     * Exposed as {@code public} so repository adapters can clear events after publishing.
     */
    @Override
    public void clearDomainEvents() {
        super.clearDomainEvents();
    }
}
