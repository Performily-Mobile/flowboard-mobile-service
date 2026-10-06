package com.performily.flowboard.benefits.domain.model.commands;

/**
 * Deactivates a benefit type: it stays in the catalog but cannot be assigned anymore.
 */
public record DeactivateBenefitTypeCommand(Long benefitTypeId) {
}
