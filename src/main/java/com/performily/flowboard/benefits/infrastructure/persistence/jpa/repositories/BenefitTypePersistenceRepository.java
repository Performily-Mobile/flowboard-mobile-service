package com.performily.flowboard.benefits.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities.BenefitTypePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BenefitTypePersistenceRepository extends JpaRepository<BenefitTypePersistenceEntity, Long> {
    List<BenefitTypePersistenceEntity> findAllByActiveTrueOrderByNameAsc();

    List<BenefitTypePersistenceEntity> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);
}
