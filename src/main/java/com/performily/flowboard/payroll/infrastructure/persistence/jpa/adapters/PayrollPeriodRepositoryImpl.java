package com.performily.flowboard.payroll.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.payroll.domain.model.entities.PayrollPeriod;
import com.performily.flowboard.payroll.domain.repositories.PayrollPeriodRepository;
import com.performily.flowboard.payroll.infrastructure.persistence.jpa.assemblers.PayrollPeriodPersistenceAssembler;
import com.performily.flowboard.payroll.infrastructure.persistence.jpa.repositories.PayrollPeriodPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Payroll Period Repository Impl
 * @summary
 * Repository adapter that bridges the payroll period domain repository port with Spring Data JPA.
 *
 * @since 1.0.0
 */
@Repository
public class PayrollPeriodRepositoryImpl implements PayrollPeriodRepository {
    private final PayrollPeriodPersistenceRepository payrollPeriodPersistenceRepository;

    /**
     * Constructor.
     *
     * @param payrollPeriodPersistenceRepository the {@link PayrollPeriodPersistenceRepository} instance
     */
    public PayrollPeriodRepositoryImpl(PayrollPeriodPersistenceRepository payrollPeriodPersistenceRepository) {
        this.payrollPeriodPersistenceRepository = payrollPeriodPersistenceRepository;
    }

    @Override
    public Optional<PayrollPeriod> findById(Long id) {
        return payrollPeriodPersistenceRepository.findById(id)
                .map(PayrollPeriodPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<PayrollPeriod> findAll() {
        return payrollPeriodPersistenceRepository.findAllMostRecentFirst().stream()
                .map(PayrollPeriodPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsByYearAndMonth(int year, int month) {
        return payrollPeriodPersistenceRepository.countByYearAndMonth(year, month) > 0;
    }

    @Override
    public PayrollPeriod save(PayrollPeriod payrollPeriod) {
        var saved = payrollPeriodPersistenceRepository.save(
                PayrollPeriodPersistenceAssembler.toPersistenceFromDomain(payrollPeriod));
        return PayrollPeriodPersistenceAssembler.toDomainFromPersistence(saved);
    }
}
