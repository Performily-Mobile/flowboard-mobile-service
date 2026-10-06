package com.performily.flowboard.payroll.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.payroll.infrastructure.persistence.jpa.entities.PayrollPeriodPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Payroll Period Persistence Repository
 * @summary
 * Spring Data repository for payroll period persistence entities.
 *
 * @since 1.0.0
 */
@Repository
public interface PayrollPeriodPersistenceRepository extends JpaRepository<PayrollPeriodPersistenceEntity, Long> {
    /**
     * Finds every payroll period, the most recent first.
     *
     * @return the list of payroll periods
     */
    @Query("SELECT pp FROM PayrollPeriodPersistenceEntity pp ORDER BY pp.period.periodYear DESC, pp.period.periodMonth DESC")
    List<PayrollPeriodPersistenceEntity> findAllMostRecentFirst();

    /**
     * Counts the payroll periods of a year and month (0 or 1).
     *
     * @param year  the year
     * @param month the month
     * @return the number of payroll periods
     */
    @Query("SELECT COUNT(pp) FROM PayrollPeriodPersistenceEntity pp " +
            "WHERE pp.period.periodYear = :year AND pp.period.periodMonth = :month")
    long countByYearAndMonth(@Param("year") int year, @Param("month") int month);
}
