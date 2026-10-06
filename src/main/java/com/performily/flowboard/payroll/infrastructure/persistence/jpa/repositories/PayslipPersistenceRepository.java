package com.performily.flowboard.payroll.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.payroll.domain.model.valueobjects.PublicationStatus;
import com.performily.flowboard.payroll.infrastructure.persistence.jpa.entities.PayslipPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Payslip Persistence Repository
 * @summary
 * Spring Data repository for payslip persistence entities.
 *
 * @since 1.0.0
 */
@Repository
public interface PayslipPersistenceRepository extends JpaRepository<PayslipPersistenceEntity, Long> {
    /**
     * Finds the payslips of an employee with a publication status whose pay period
     * is in the given year, the most recent period first.
     *
     * @param employeeId the employee id
     * @param status     the publication status
     * @param year       the pay period year
     * @return the list of payslips
     */
    @Query("SELECT p FROM PayslipPersistenceEntity p " +
            "WHERE p.employeeId = :employeeId AND p.publicationStatus = :status " +
            "AND p.payrollPeriod.period.periodYear = :year " +
            "ORDER BY p.payrollPeriod.period.periodMonth DESC")
    List<PayslipPersistenceEntity> findAllByEmployeeIdAndStatusAndYear(@Param("employeeId") Long employeeId,
                                                                      @Param("status") PublicationStatus status,
                                                                      @Param("year") int year);

    /**
     * Finds every payslip of a payroll period.
     *
     * @param payrollPeriodId the payroll period id
     * @return the list of payslips
     */
    @Query("SELECT p FROM PayslipPersistenceEntity p WHERE p.payrollPeriod.id = :payrollPeriodId ORDER BY p.id")
    List<PayslipPersistenceEntity> findAllByPayrollPeriodId(@Param("payrollPeriodId") Long payrollPeriodId);

    /**
     * Counts the payslips of an employee for a payroll period (0 or 1).
     *
     * @param employeeId      the employee id
     * @param payrollPeriodId the payroll period id
     * @return the number of payslips
     */
    @Query("SELECT COUNT(p) FROM PayslipPersistenceEntity p " +
            "WHERE p.employeeId = :employeeId AND p.payrollPeriod.id = :payrollPeriodId")
    long countByEmployeeIdAndPayrollPeriodId(@Param("employeeId") Long employeeId,
                                                 @Param("payrollPeriodId") Long payrollPeriodId);
}
