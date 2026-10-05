package com.performily.flowboard.benefits.infrastructure.persistence.jpa.assemblers;

import com.performily.flowboard.benefits.domain.model.entities.BenefitType;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities.BenefitTypePersistenceEntity;

/**
 * Converts between the BenefitType entity and its persistence entity.
 */
public final class BenefitTypePersistenceAssembler {
    private BenefitTypePersistenceAssembler() {
    }

    public static BenefitType toDomainFromPersistence(BenefitTypePersistenceEntity entity) {
        return new BenefitType(entity.getId(), entity.getName(), entity.getDescription(), entity.isHasBalance(),
                entity.getUnit(), entity.isActive());
    }

    public static BenefitTypePersistenceEntity toPersistenceFromDomain(BenefitType benefitType) {
        var entity = new BenefitTypePersistenceEntity();
        entity.setId(benefitType.getId());
        entity.setName(benefitType.getName());
        entity.setDescription(benefitType.getDescription());
        entity.setHasBalance(benefitType.hasBalance());
        entity.setUnit(benefitType.getUnit());
        entity.setActive(benefitType.isActive());
        return entity;
    }
}
