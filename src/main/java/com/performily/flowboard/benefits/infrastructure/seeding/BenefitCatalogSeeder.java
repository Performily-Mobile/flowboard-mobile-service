package com.performily.flowboard.benefits.infrastructure.seeding;

import com.performily.flowboard.benefits.application.commandservices.BenefitCommandService;
import com.performily.flowboard.benefits.domain.model.commands.CreateBenefitTypeCommand;
import com.performily.flowboard.benefits.domain.model.valueobjects.BenefitUnit;
import com.performily.flowboard.benefits.domain.repositories.BenefitTypeRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Loads the starting benefit catalog shown in the prototype when the catalog is empty.
 * Disable it with benefits.seed.catalog-enabled=false.
 */
@Component
@ConditionalOnProperty(name = "benefits.seed.catalog-enabled", havingValue = "true", matchIfMissing = true)
public class BenefitCatalogSeeder {
    private static final List<CreateBenefitTypeCommand> CATALOG = List.of(
            new CreateBenefitTypeCommand("Gratificación", "Pago de julio y diciembre", false, BenefitUnit.MONEY),
            new CreateBenefitTypeCommand("Canasta navideña", "Entrega anual en diciembre", false, BenefitUnit.UNITS),
            new CreateBenefitTypeCommand("Vales de consumo", "Vales mensuales de consumo", false, BenefitUnit.MONEY),
            new CreateBenefitTypeCommand("Licencia por cumpleaños", "Día libre por cumpleaños", true, BenefitUnit.DAYS)
    );

    private final BenefitCommandService benefitCommandService;
    private final BenefitTypeRepository benefitTypeRepository;

    public BenefitCatalogSeeder(BenefitCommandService benefitCommandService, BenefitTypeRepository benefitTypeRepository) {
        this.benefitCommandService = benefitCommandService;
        this.benefitTypeRepository = benefitTypeRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedCatalog() {
        if (!benefitTypeRepository.findAll().isEmpty()) {
            return;
        }
        CATALOG.forEach(benefitCommandService::handle);
    }
}
