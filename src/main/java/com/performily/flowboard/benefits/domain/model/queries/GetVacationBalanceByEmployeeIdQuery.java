package com.performily.flowboard.benefits.domain.model.queries;

/**
 * Vacation balance of one employee (US41, scenario 1). Also used by Request
 * through BenefitsContextFacade.
 */
public record GetVacationBalanceByEmployeeIdQuery(Long employeeId) {
}
