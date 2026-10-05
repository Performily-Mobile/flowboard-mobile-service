package com.performily.flowboard.benefits.domain.repositories;

import com.performily.flowboard.benefits.domain.model.aggregates.VacationBalance;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Persistence contract of the VacationBalance aggregate.
 * Saving a balance also publishes its domain events.
 */
public interface VacationBalanceRepository {
    VacationBalance save(VacationBalance balance);

    Optional<VacationBalance> findByEmployeeId(Long employeeId);

    List<VacationBalance> findAllByEmployeeIds(Collection<Long> employeeIds);

    boolean existsByEmployeeId(Long employeeId);
}
