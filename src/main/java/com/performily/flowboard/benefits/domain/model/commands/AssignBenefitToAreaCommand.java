package com.performily.flowboard.benefits.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Assigns a benefit to every active employee of an area (US38, scenario 2).
 * Employees that already have the benefit in the period are skipped.
 */
public record AssignBenefitToAreaCommand(Long benefitTypeId, Long areaId, BigDecimal quantity,
                                         LocalDate startDate, LocalDate endDate) {
}
