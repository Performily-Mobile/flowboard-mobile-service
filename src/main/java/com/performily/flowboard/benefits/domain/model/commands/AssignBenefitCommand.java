package com.performily.flowboard.benefits.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Assigns a benefit to one employee (US38, scenario 1).
 */
public record AssignBenefitCommand(Long benefitTypeId, Long employeeId, BigDecimal quantity,
                                   LocalDate startDate, LocalDate endDate) {
}
