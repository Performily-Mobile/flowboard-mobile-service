package com.performily.flowboard.payroll.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.domain.model.valueobjects.PublicationStatus;
import com.performily.flowboard.payroll.domain.repositories.PayslipRepository;
import com.performily.flowboard.payroll.infrastructure.persistence.jpa.assemblers.PayslipPersistenceAssembler;
import com.performily.flowboard.payroll.infrastructure.persistence.jpa.repositories.PayrollPeriodPersistenceRepository;
import com.performily.flowboard.payroll.infrastructure.persistence.jpa.repositories.PayslipPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Payslip Repository Impl
 * @summary
 * Repository adapter that bridges the payslip domain repository port with Spring Data JPA.
 *
 * Also acts as the event-publishing boundary: after the {@link Payslip} is persisted,
 * the domain events registered by the aggregate are dispatched via Spring's
 * {@link ApplicationEventPublisher}.
 *
 * @since 1.0.0
 */
@Repository
public class PayslipRepositoryImpl implements PayslipRepository {
    private final PayslipPersistenceRepository payslipPersistenceRepository;
    private final PayrollPeriodPersistenceRepository payrollPeriodPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor.
     *
     * @param payslipPersistenceRepository       the {@link PayslipPersistenceRepository} instance
     * @param payrollPeriodPersistenceRepository the {@link PayrollPeriodPersistenceRepository} instance
     * @param eventPublisher                     the {@link ApplicationEventPublisher} instance
     */
    public PayslipRepositoryImpl(PayslipPersistenceRepository payslipPersistenceRepository,
                                 PayrollPeriodPersistenceRepository payrollPeriodPersistenceRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.payslipPersistenceRepository = payslipPersistenceRepository;
        this.payrollPeriodPersistenceRepository = payrollPeriodPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Payslip> findById(Long id) {
        return payslipPersistenceRepository.findById(id).map(PayslipPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Payslip> findAllPublishedByEmployeeIdAndYear(Long employeeId, int year) {
        return payslipPersistenceRepository
                .findAllByEmployeeIdAndStatusAndYear(employeeId, PublicationStatus.PUBLISHED, year).stream()
                .map(PayslipPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Payslip> findAllByPayrollPeriodId(Long payrollPeriodId) {
        return payslipPersistenceRepository.findAllByPayrollPeriodId(payrollPeriodId).stream()
                .map(PayslipPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsByEmployeeIdAndPayrollPeriodId(Long employeeId, Long payrollPeriodId) {
        return payslipPersistenceRepository.countByEmployeeIdAndPayrollPeriodId(employeeId, payrollPeriodId) > 0;
    }

    @Override
    public Payslip save(Payslip payslip) {
        var pendingEvents = new ArrayList<>(payslip.domainEvents());
        payslip.clearDomainEvents();
        var payrollPeriod = payrollPeriodPersistenceRepository.getReferenceById(payslip.getPayrollPeriod().getId());
        var savedEntity = payslipPersistenceRepository.saveAndFlush(
                PayslipPersistenceAssembler.toPersistenceFromDomain(payslip, payrollPeriod));
        pendingEvents.forEach(eventPublisher::publishEvent);
        return PayslipPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
}
