package com.performily.flowboard.benefits.interfaces.rest.transform;

import com.performily.flowboard.benefits.domain.model.commands.CreateBenefitTypeCommand;
import com.performily.flowboard.benefits.domain.model.entities.BenefitType;
import com.performily.flowboard.benefits.domain.model.valueobjects.BenefitUnit;
import com.performily.flowboard.benefits.interfaces.rest.resources.BenefitTypeResource;
import com.performily.flowboard.benefits.interfaces.rest.resources.CreateBenefitTypeResource;

public final class BenefitTypeResourceFromEntityAssembler {
    private BenefitTypeResourceFromEntityAssembler() {
    }

    public static BenefitTypeResource toResourceFromEntity(BenefitType entity) {
        return new BenefitTypeResource(entity.getId(), entity.getName(), entity.getDescription(),
                entity.getUnit().name(), entity.hasBalance(), entity.isActive());
    }

    public static CreateBenefitTypeCommand toCommandFromResource(CreateBenefitTypeResource resource) {
        var unit = BenefitsEnumAssembler.toEnum(BenefitUnit.class, resource.unit(), "unit");
        return new CreateBenefitTypeCommand(resource.name(), resource.description(),
                Boolean.TRUE.equals(resource.hasBalance()), unit);
    }
}
