package com.performily.flowboard.benefits.domain.model.commands;

import com.performily.flowboard.benefits.domain.model.valueobjects.BenefitUnit;

/**
 * Creates a benefit type in the catalog (US37).
 */
public record CreateBenefitTypeCommand(String name, String description, boolean hasBalance, BenefitUnit unit) {
}
