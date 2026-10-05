package com.performily.flowboard.benefits.domain.model.commands;

import java.math.BigDecimal;

/**
 * Takes the days of an approved vacation request (US41, scenario 2).
 * Sent by RequestApprovedEventHandler.
 */
public record UseVacationDaysCommand(Long employeeId, BigDecimal days, Long requestId) {
}
