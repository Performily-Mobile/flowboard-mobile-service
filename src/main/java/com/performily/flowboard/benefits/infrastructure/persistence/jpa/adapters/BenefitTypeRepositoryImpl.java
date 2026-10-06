package com.performily.flowboard.benefits.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.benefits.domain.model.entities.BenefitType;
import com.performily.flowboard.benefits.domain.repositories.BenefitTypeRepository;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.assemblers.BenefitTypePersistenceAssembler;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.repositories.BenefitTypePersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of {@link BenefitTypeRepository}.
 */
@Repository
@Transactional
public class BenefitTypeRepositoryImpl implements BenefitTypeRepository {
    private final BenefitTypePersistenceRepository benefitTypePersistenceRepository;

    public BenefitTypeRepositoryImpl(BenefitTypePersistenceRepository benefitTypePersistenceRepository) {
        this.benefitTypePersistenceRepository = benefitTypePersistenceRepository;
    }

    @Override
    public BenefitType save(BenefitType benefitType) {
        var saved = benefitTypePersistenceRepository.saveAndFlush(
                BenefitTypePersistenceAssembler.toPersistenceFromDomain(benefitType));
        return BenefitTypePersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public Optional<BenefitType> findById(Long id) {
        return id == null ? Optional.empty()
                : benefitTypePersistenceRepository.findById(id).map(BenefitTypePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<BenefitType> findAll() {
        return benefitTypePersistenceRepository.findAllByOrderByNameAsc().stream()
                .map(BenefitTypePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<BenefitType> findAllByActiveTrue() {
        return benefitTypePersistenceRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(BenefitTypePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsByName(String name) {
        return benefitTypePersistenceRepository.existsByNameIgnoreCase(name);
    }
}
