package com.performily.flowboard.benefits.domain.services;

import com.performily.flowboard.benefits.domain.model.valueobjects.VacationDays;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Vacation Accrual Policy
 * @summary
 * Domain service with the accrual rules of the vacation balance (spike SP04).
 *
 * In Peru a worker earns 30 calendar days of vacation for each complete year of
 * service (Legislative Decree 713). Flowboard spreads them as 2.5 days per month,
 * as the vacation balance screen shows ("Acumulación mensual +2.5 días").
 *
 * When a balance is opened for an employee that already works in the company, it
 * starts with the days of the current vacation year only: completed months of
 * service, at most 12 (30 days). Older periods are considered settled outside
 * Flowboard and HR can correct them with a manual adjustment.
 *
 * It has no state and does not depend on Spring, so it is a plain domain object.
 *
 * @since 1.0.0
 */
public final class VacationAccrualPolicy {
    public static final VacationDays MONTHLY_DAYS = VacationDays.of("2.5");
    public static final int MAX_INITIAL_MONTHS = 12;

    /**
     * Days earned before the balance existed.
     *
     * @param hireDate the hire date of the employee
     * @param openedOn the date the balance is opened
     * @return completed months of service (at most 12) multiplied by 2.5
     */
    public VacationDays initialAccrual(LocalDate hireDate, LocalDate openedOn) {
        if (hireDate == null || openedOn == null || !hireDate.isBefore(openedOn)) {
            return VacationDays.ZERO;
        }
        long months = Math.min(ChronoUnit.MONTHS.between(hireDate, openedOn), MAX_INITIAL_MONTHS);
        return new VacationDays(MONTHLY_DAYS.value().multiply(BigDecimal.valueOf(months)));
    }

    /**
     * Days earned in one month of service.
     *
     * @return 2.5 days
     */
    public VacationDays monthlyAccrual() {
        return MONTHLY_DAYS;
    }

    /**
     * An employee hired during the month being accrued already got that month
     * when the balance was opened, so only employees hired before it accrue.
     *
     * @param hireDate    the hire date of the employee
     * @param accrualDate any date of the month to accrue
     * @return true when the employee must receive the monthly days
     */
    public boolean accruesInMonthOf(LocalDate hireDate, LocalDate accrualDate) {
        return hireDate != null && hireDate.isBefore(accrualDate.withDayOfMonth(1));
    }
}
