package com.performily.flowboard.benefits.domain.repositories;

import com.performily.flowboard.benefits.domain.model.entities.BenefitType;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract of the benefit catalog.
 */
public interface BenefitTypeRepository {
    BenefitType save(BenefitType benefitType);

    Optional<BenefitType> findById(Long id);

    List<BenefitType> findAll();

    List<BenefitType> findAllByActiveTrue();

    boolean existsByName(String name);
}
