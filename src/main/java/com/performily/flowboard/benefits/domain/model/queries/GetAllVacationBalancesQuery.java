package com.performily.flowboard.benefits.domain.model.queries;

/**
 * Vacation balances of the active employees, optionally of one area,
 * from the highest to the lowest available days.
 */
public record GetAllVacationBalancesQuery(Long areaId) {
}
