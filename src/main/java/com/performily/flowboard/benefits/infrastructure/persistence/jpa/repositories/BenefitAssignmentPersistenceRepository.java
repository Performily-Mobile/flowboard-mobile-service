package com.performily.flowboard.benefits.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.benefits.domain.model.valueobjects.AssignmentStatus;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities.BenefitAssignmentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface BenefitAssignmentPersistenceRepository extends JpaRepository<BenefitAssignmentPersistenceEntity, Long> {

    List<BenefitAssignmentPersistenceEntity> findAllByEmployeeIdOrderByStartDateDesc(Long employeeId);

    List<BenefitAssignmentPersistenceEntity> findAllByEmployeeIdAndStatus(Long employeeId, AssignmentStatus status);

    @Query("""
            select a from BenefitAssignmentPersistenceEntity a
            where (:status is null or a.status = :status)
              and (:benefitTypeId is null or a.benefitType.id = :benefitTypeId)
            order by a.startDate desc, a.id desc
            """)
    List<BenefitAssignmentPersistenceEntity> findAllByOptionalStatusAndType(@Param("status") AssignmentStatus status,
                                                                            @Param("benefitTypeId") Long benefitTypeId);

    /**
     * Employees (of the given ones) with a not cancelled assignment of the type whose
     * validity overlaps [startDate, endDate].
     */
    @Query("""
            select distinct a.employeeId from BenefitAssignmentPersistenceEntity a
            where a.employeeId in :employeeIds
              and a.benefitType.id = :benefitTypeId
              and a.status <> :cancelled
              and a.startDate <= :endDate
              and a.endDate >= :startDate
            """)
    List<Long> findEmployeeIdsWithTypeInPeriod(@Param("employeeIds") Collection<Long> employeeIds,
                                               @Param("benefitTypeId") Long benefitTypeId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               @Param("cancelled") AssignmentStatus cancelled);
}
