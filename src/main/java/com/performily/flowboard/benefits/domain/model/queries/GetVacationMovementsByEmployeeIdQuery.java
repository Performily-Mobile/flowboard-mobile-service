package com.performily.flowboard.benefits.domain.model.queries;

import java.time.LocalDate;

/**
 * Movements of a vacation balance, optionally between two dates.
 */
public record GetVacationMovementsByEmployeeIdQuery(Long employeeId, LocalDate fromDate, LocalDate toDate) {
}
