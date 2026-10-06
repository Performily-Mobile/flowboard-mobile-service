package com.performily.flowboard.payroll.interfaces.rest.transform;

import com.performily.flowboard.payroll.domain.model.entities.PayrollPeriod;
import com.performily.flowboard.payroll.interfaces.rest.resources.PayrollPeriodResource;

/**
 * Payroll Period Resource From Entity Assembler
 * @summary
 * Assembler to convert a PayrollPeriod entity to a PayrollPeriodResource.
 *
 * @since 1.0.0
 */
public class PayrollPeriodResourceFromEntityAssembler {
    /**
     * Converts a {@link PayrollPeriod} to a {@link PayrollPeriodResource}.
     *
     * @param entity the {@link PayrollPeriod} instance
     * @return the {@link PayrollPeriodResource}
     */
    public static PayrollPeriodResource toResourceFromEntity(PayrollPeriod entity) {
        var period = entity.getPeriod();
        return new PayrollPeriodResource(entity.getId(), period.year(), period.month(), period.label(),
                entity.getScheduledPaymentDate());
    }
}
