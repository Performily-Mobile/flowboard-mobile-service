package com.performily.flowboard.benefits.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities.VacationBalancePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface VacationBalancePersistenceRepository extends JpaRepository<VacationBalancePersistenceEntity, Long> {
    Optional<VacationBalancePersistenceEntity> findByEmployeeId(Long employeeId);

    List<VacationBalancePersistenceEntity> findAllByEmployeeIdIn(Collection<Long> employeeIds);

    boolean existsByEmployeeId(Long employeeId);
}
