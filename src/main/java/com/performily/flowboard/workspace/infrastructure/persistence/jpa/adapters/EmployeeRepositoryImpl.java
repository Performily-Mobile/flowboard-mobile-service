package com.performily.flowboard.workspace.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.shared.domain.model.valueobjects.EmailAddress;
import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import com.performily.flowboard.workspace.domain.model.valueobjects.EmploymentStatus;
import com.performily.flowboard.workspace.domain.model.valueobjects.IdentityDocument;
import com.performily.flowboard.workspace.domain.repositories.EmployeeRepository;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.assemblers.EmployeePersistenceAssembler;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.repositories.EmployeePersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Employee Repository Impl
 * @summary
 * Repository adapter that bridges the employee domain repository port with Spring Data JPA.
 *
 * Also acts as the event-publishing boundary: after the {@link Employee} is persisted,
 * the domain events registered by the aggregate are dispatched via Spring's
 * {@link ApplicationEventPublisher}. For a brand-new employee, an
 * EmployeeRegisteredEvent is added once the JPA-assigned id is available.
 *
 * Methods run inside a transaction so the lazy job history and documents
 * can be read while assembling the domain object.
 *
 * @since 1.0.0
 */
@Repository
@Transactional
public class EmployeeRepositoryImpl implements EmployeeRepository {
    private final EmployeePersistenceRepository employeePersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor.
     *
     * @param employeePersistenceRepository the {@link EmployeePersistenceRepository} instance
     * @param eventPublisher the {@link ApplicationEventPublisher} instance
     */
    public EmployeeRepositoryImpl(EmployeePersistenceRepository employeePersistenceRepository,
                                  ApplicationEventPublisher eventPublisher) {
        this.employeePersistenceRepository = employeePersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return employeePersistenceRepository.findById(id).map(EmployeePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Employee> findAll() {
        return employeePersistenceRepository.findAll().stream().map(EmployeePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Employee> findAllByFilters(String search, Long areaId, EmploymentStatus status, Long positionId) {
        var pattern = search == null ? null : "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
        return employeePersistenceRepository.findAllByFilters(pattern, areaId, status, positionId).stream()
                .map(EmployeePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Employee> findAllByDirectManagerId(Long managerId) {
        return employeePersistenceRepository.findAllByDirectManagerId(managerId).stream()
                .map(EmployeePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Employee> findAllByStatusNot(EmploymentStatus status) {
        return employeePersistenceRepository.findAllByStatusNot(status).stream()
                .map(EmployeePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Employee save(Employee employee) {
        boolean isNew = employee.getId() == null;
        var pendingEvents = new ArrayList<>(employee.domainEvents());
        employee.clearDomainEvents();
        var savedEntity = employeePersistenceRepository.saveAndFlush(EmployeePersistenceAssembler.toPersistenceFromDomain(employee));
        var savedEmployee = EmployeePersistenceAssembler.toDomainFromPersistence(savedEntity);
        if (isNew) {
            savedEmployee.onRegistered();
            pendingEvents.addAll(savedEmployee.domainEvents());
            savedEmployee.clearDomainEvents();
        }
        pendingEvents.forEach(eventPublisher::publishEvent);
        return savedEmployee;
    }

    @Override
    public boolean existsById(Long id) {
        return employeePersistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return employeePersistenceRepository.countByEmail(email) > 0;
    }

    @Override
    public boolean existsByEmailAndIdIsNot(EmailAddress email, Long id) {
        return employeePersistenceRepository.countByEmailAndIdIsNot(email, id) > 0;
    }

    @Override
    public boolean existsByIdentityDocumentAndStatus(IdentityDocument identityDocument, EmploymentStatus status) {
        return employeePersistenceRepository.countByIdentityDocumentAndStatus(
                identityDocument.type(), identityDocument.number(), status) > 0;
    }

    @Override
    public boolean existsByAreaIdAndStatus(Long areaId, EmploymentStatus status) {
        return employeePersistenceRepository.countByAreaIdAndStatus(areaId, status) > 0;
    }

    @Override
    public long countByAreaIdAndStatus(Long areaId, EmploymentStatus status) {
        return employeePersistenceRepository.countByAreaIdAndStatus(areaId, status);
    }

    @Override
    public boolean existsByDirectManagerIdAndStatusNot(Long managerId, EmploymentStatus status) {
        return employeePersistenceRepository.countByDirectManagerIdAndStatusNot(managerId, status) > 0;
    }
}