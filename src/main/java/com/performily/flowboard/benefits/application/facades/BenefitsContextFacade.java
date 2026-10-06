package com.performily.flowboard.benefits.application.facades;

import java.math.BigDecimal;

/**
 * Benefits Context Facade
 * @summary
 * What Benefits offers to other bounded contexts. Request uses it before accepting
 * a vacation request, to check synchronously that the employee has enough days
 * (Partnership Request ↔ Benefits in the Context Map). It is read only: the days are
 * debited when Request publishes RequestApprovedEvent.
 *
 * @since 1.0.0
 */
public interface BenefitsContextFacade {
    /**
     * Checks whether the employee has at least the given available vacation days.
     *
     * @param employeeId the employee
     * @param days       the days of the request
     * @return true when the balance exists and has enough available days
     */
    boolean hasAvailableVacationDays(Long employeeId, BigDecimal days);

    /**
     * Available vacation days of the employee.
     *
     * @param employeeId the employee
     * @return the available days, zero when the employee has no balance
     */
    BigDecimal fetchAvailableDays(Long employeeId);
}
