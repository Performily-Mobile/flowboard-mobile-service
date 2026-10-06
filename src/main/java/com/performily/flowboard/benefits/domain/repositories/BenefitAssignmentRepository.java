package com.performily.flowboard.benefits.domain.repositories;

import com.performily.flowboard.benefits.domain.model.aggregates.BenefitAssignment;
import com.performily.flowboard.benefits.domain.model.valueobjects.AssignmentStatus;
import com.performily.flowboard.shared.domain.model.valueobjects.DateRange;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Persistence contract of the BenefitAssignment aggregate.
 * Saving an assignment also publishes its domain events.
 */
public interface BenefitAssignmentRepository {
    BenefitAssignment save(BenefitAssignment assignment);

    Optional<BenefitAssignment> findById(Long id);

    List<BenefitAssignment> findAllByEmployeeId(Long employeeId);

    List<BenefitAssignment> findAll(AssignmentStatus status, Long benefitTypeId);

    List<BenefitAssignment> findAllByEmployeeIdAndStatus(Long employeeId, AssignmentStatus status);

    /**
     * Checks whether the employee already has a not cancelled assignment of the
     * type whose validity overlaps the period.
     */
    boolean existsByEmployeeIdAndTypeAndPeriod(Long employeeId, Long benefitTypeId, DateRange period);

    /**
     * Of the given employees, the ones that already have a not cancelled assignment
     * of the type whose validity overlaps the period.
     */
    Set<Long> findEmployeeIdsWithTypeInPeriod(Collection<Long> employeeIds, Long benefitTypeId, DateRange period);
}
