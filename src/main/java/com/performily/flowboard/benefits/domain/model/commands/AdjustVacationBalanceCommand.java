package com.performily.flowboard.benefits.domain.model.commands;

import java.math.BigDecimal;

/**
 * Manual adjustment of a vacation balance made by HR staff (US42).
 *
 * @param days     signed days: positive adds, negative discounts
 * @param reason   required reason
 * @param authorId the HR employee that makes it
 */
public record AdjustVacationBalanceCommand(Long employeeId, BigDecimal days, String reason, Long authorId) {
}
