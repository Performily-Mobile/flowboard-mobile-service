package com.performily.flowboard.benefits.domain.model.commands;

/**
 * Returns the days of an approved request that was annulled (US41, scenario 3).
 * Sent by RequestCancelledEventHandler.
 */
public record ReverseVacationUsageCommand(Long employeeId, Long requestId) {
}
