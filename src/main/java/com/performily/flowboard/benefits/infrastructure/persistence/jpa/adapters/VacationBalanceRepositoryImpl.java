package com.performily.flowboard.benefits.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.benefits.domain.model.aggregates.VacationBalance;
import com.performily.flowboard.benefits.domain.repositories.VacationBalanceRepository;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.assemblers.VacationBalancePersistenceAssembler;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.repositories.VacationBalancePersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of {@link VacationBalanceRepository}.
 *
 * Also acts as the event-publishing boundary: after the balance is persisted,
 * its VacationBalanceUpdatedEvent events are published.
 */
@Repository
@Transactional
public class VacationBalanceRepositoryImpl implements VacationBalanceRepository {
    private final VacationBalancePersistenceRepository vacationBalancePersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public VacationBalanceRepositoryImpl(VacationBalancePersistenceRepository vacationBalancePersistenceRepository,
                                         ApplicationEventPublisher eventPublisher) {
        this.vacationBalancePersistenceRepository = vacationBalancePersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public VacationBalance save(VacationBalance balance) {
        var pendingEvents = new ArrayList<>(balance.domainEvents());
        balance.clearDomainEvents();
        var savedEntity = vacationBalancePersistenceRepository.saveAndFlush(
                VacationBalancePersistenceAssembler.toPersistenceFromDomain(balance));
        var savedBalance = VacationBalancePersistenceAssembler.toDomainFromPersistence(savedEntity);
        pendingEvents.forEach(eventPublisher::publishEvent);
        return savedBalance;
    }

    @Override
    public Optional<VacationBalance> findByEmployeeId(Long employeeId) {
        return employeeId == null ? Optional.empty()
                : vacationBalancePersistenceRepository.findByEmployeeId(employeeId)
                .map(VacationBalancePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<VacationBalance> findAllByEmployeeIds(Collection<Long> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return List.of();
        }
        return vacationBalancePersistenceRepository.findAllByEmployeeIdIn(employeeIds).stream()
                .map(VacationBalancePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsByEmployeeId(Long employeeId) {
        return vacationBalancePersistenceRepository.existsByEmployeeId(employeeId);
    }
}
