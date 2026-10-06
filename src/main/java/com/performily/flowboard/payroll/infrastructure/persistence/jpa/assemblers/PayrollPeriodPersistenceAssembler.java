package com.performily.flowboard.payroll.infrastructure.persistence.jpa.assemblers;

import com.performily.flowboard.payroll.domain.model.entities.PayrollPeriod;
import com.performily.flowboard.payroll.domain.model.valueobjects.PayPeriod;
import com.performily.flowboard.payroll.infrastructure.persistence.jpa.embeddables.PayPeriodPersistenceEmbeddable;
import com.performily.flowboard.payroll.infrastructure.persistence.jpa.entities.PayrollPeriodPersistenceEntity;

/**
 * Payroll Period Persistence Assembler
 * @summary
 * Static assembler between payroll period domain and persistence representations.
 *
 * @since 1.0.0
 */
public final class PayrollPeriodPersistenceAssembler {
    private PayrollPeriodPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity to its domain representation.
     *
     * @param entity the {@link PayrollPeriodPersistenceEntity} instance
     * @return the domain object, or null when the entity is null
     */
    public static PayrollPeriod toDomainFromPersistence(PayrollPeriodPersistenceEntity entity) {
        if (entity == null) return null;
        var period = entity.getPeriod();
        return new PayrollPeriod(
                entity.getId(),
                new PayPeriod(period.getPeriodYear(), period.getPeriodMonth()),
                entity.getScheduledPaymentDate());
    }

    /**
     * Converts a domain object to its persistence representation.
     *
     * @param payrollPeriod the {@link PayrollPeriod} instance
     * @return the persistence entity, or null when the domain object is null
     */
    public static PayrollPeriodPersistenceEntity toPersistenceFromDomain(PayrollPeriod payrollPeriod) {
        if (payrollPeriod == null) return null;
        var entity = new PayrollPeriodPersistenceEntity();
        if (payrollPeriod.getId() != null) {
            entity.setId(payrollPeriod.getId());
        }
        entity.setPeriod(new PayPeriodPersistenceEmbeddable(
                payrollPeriod.getPeriod().year(), payrollPeriod.getPeriod().month()));
        entity.setScheduledPaymentDate(payrollPeriod.getScheduledPaymentDate());
        return entity;
    }
}
