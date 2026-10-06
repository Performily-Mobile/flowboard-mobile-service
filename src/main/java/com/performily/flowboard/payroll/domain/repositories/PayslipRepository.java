package com.performily.flowboard.payroll.domain.repositories;

import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;

import java.util.List;
import java.util.Optional;

/**
 * Payslip Repository
 * @summary
 * Payroll payslip repository port.
 *
 * @since 1.0.0
 */
public interface PayslipRepository {
    /**
     * Finds the payslip with the given id.
     *
     * @param id the id
     * @return the payslip, if found
     */
    Optional<Payslip> findById(Long id);

    /**
     * Finds the PUBLISHED payslips of an employee whose pay period is in the given year,
     * the most recent period first.
     *
     * @param employeeId the employee id
     * @param year       the pay period year
     * @return the list of payslips
     */
    List<Payslip> findAllPublishedByEmployeeIdAndYear(Long employeeId, int year);

    /**
     * Finds every payslip of a payroll period.
     *
     * @param payrollPeriodId the payroll period id
     * @return the list of payslips
     */
    List<Payslip> findAllByPayrollPeriodId(Long payrollPeriodId);

    /**
     * Checks whether an employee already has a payslip for a payroll period.
     *
     * @param employeeId      the employee id
     * @param payrollPeriodId the payroll period id
     * @return true if it exists
     */
    boolean existsByEmployeeIdAndPayrollPeriodId(Long employeeId, Long payrollPeriodId);

    /**
     * Saves the payslip and publishes its domain events.
     *
     * @param payslip the {@link Payslip} instance
     * @return the saved payslip
     */
    Payslip save(Payslip payslip);
}
