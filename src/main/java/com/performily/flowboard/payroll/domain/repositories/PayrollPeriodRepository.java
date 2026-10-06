package com.performily.flowboard.payroll.domain.repositories;

import com.performily.flowboard.payroll.domain.model.entities.PayrollPeriod;

import java.util.List;
import java.util.Optional;

/**
 * Payroll Period Repository
 * @summary
 * Payroll period repository port.
 *
 * @since 1.0.0
 */
public interface PayrollPeriodRepository {
    /**
     * Finds the payroll period with the given id.
     *
     * @param id the id
     * @return the payroll period, if found
     */
    Optional<PayrollPeriod> findById(Long id);

    /**
     * Finds every payroll period, the most recent first.
     *
     * @return the list of payroll periods
     */
    List<PayrollPeriod> findAll();

    /**
     * Checks whether a payroll period exists for a year and month.
     *
     * @param year  the year
     * @param month the month
     * @return true if it exists
     */
    boolean existsByYearAndMonth(int year, int month);

    /**
     * Saves the payroll period.
     *
     * @param payrollPeriod the {@link PayrollPeriod} instance
     * @return the saved payroll period
     */
    PayrollPeriod save(PayrollPeriod payrollPeriod);
}
