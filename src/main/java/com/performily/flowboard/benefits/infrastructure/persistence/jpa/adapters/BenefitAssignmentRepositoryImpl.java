package com.performily.flowboard.benefits.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.benefits.domain.model.aggregates.BenefitAssignment;
import com.performily.flowboard.benefits.domain.model.valueobjects.AssignmentStatus;
import com.performily.flowboard.benefits.domain.repositories.BenefitAssignmentRepository;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.assemblers.BenefitAssignmentPersistenceAssembler;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.repositories.BenefitAssignmentPersistenceRepository;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.repositories.BenefitTypePersistenceRepository;
import com.performily.flowboard.shared.domain.model.valueobjects.DateRange;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * JPA implementation of {@link BenefitAssignmentRepository}.
 *
 * Also acts as the event-publishing boundary: after the assignment is persisted,
 * its domain events are published with the {@link ApplicationEventPublisher}.
 * For a new assignment, BenefitAssignedEvent is registered once it has an id.
 */
@Repository
@Transactional
public class BenefitAssignmentRepositoryImpl implements BenefitAssignmentRepository {
    private final BenefitAssignmentPersistenceRepository benefitAssignmentPersistenceRepository;
    private final BenefitTypePersistenceRepository benefitTypePersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BenefitAssignmentRepositoryImpl(BenefitAssignmentPersistenceRepository benefitAssignmentPersistenceRepository,
                                           BenefitTypePersistenceRepository benefitTypePersistenceRepository,
                                           ApplicationEventPublisher eventPublisher) {
        this.benefitAssignmentPersistenceRepository = benefitAssignmentPersistenceRepository;
        this.benefitTypePersistenceRepository = benefitTypePersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public BenefitAssignment save(BenefitAssignment assignment) {
        boolean isNew = assignment.getId() == null;
        var pendingEvents = new ArrayList<>(assignment.domainEvents());
        assignment.clearDomainEvents();
        var benefitType = benefitTypePersistenceRepository.getReferenceById(assignment.getBenefitType().getId());
        var savedEntity = benefitAssignmentPersistenceRepository.saveAndFlush(
                BenefitAssignmentPersistenceAssembler.toPersistenceFromDomain(assignment, benefitType));
        var savedAssignment = BenefitAssignmentPersistenceAssembler.toDomainFromPersistence(savedEntity);
        if (isNew) {
            savedAssignment.onAssigned();
            pendingEvents.addAll(savedAssignment.domainEvents());
            savedAssignment.clearDomainEvents();
        }
        pendingEvents.forEach(eventPublisher::publishEvent);
        return savedAssignment;
    }

    @Override
    public Optional<BenefitAssignment> findById(Long id) {
        return id == null ? Optional.empty()
                : benefitAssignmentPersistenceRepository.findById(id).map(BenefitAssignmentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<BenefitAssignment> findAllByEmployeeId(Long employeeId) {
        return benefitAssignmentPersistenceRepository.findAllByEmployeeIdOrderByStartDateDesc(employeeId).stream()
                .map(BenefitAssignmentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<BenefitAssignment> findAll(AssignmentStatus status, Long benefitTypeId) {
        return benefitAssignmentPersistenceRepository.findAllByOptionalStatusAndType(status, benefitTypeId).stream()
                .map(BenefitAssignmentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<BenefitAssignment> findAllByEmployeeIdAndStatus(Long employeeId, AssignmentStatus status) {
        return benefitAssignmentPersistenceRepository.findAllByEmployeeIdAndStatus(employeeId, status).stream()
                .map(BenefitAssignmentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsByEmployeeIdAndTypeAndPeriod(Long employeeId, Long benefitTypeId, DateRange period) {
        return !findEmployeeIdsWithTypeInPeriod(List.of(employeeId), benefitTypeId, period).isEmpty();
    }

    @Override
    public Set<Long> findEmployeeIdsWithTypeInPeriod(Collection<Long> employeeIds, Long benefitTypeId, DateRange period) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(benefitAssignmentPersistenceRepository.findEmployeeIdsWithTypeInPeriod(
                employeeIds, benefitTypeId, period.startDate(), period.endDate(), AssignmentStatus.CANCELLED));
    }
}
