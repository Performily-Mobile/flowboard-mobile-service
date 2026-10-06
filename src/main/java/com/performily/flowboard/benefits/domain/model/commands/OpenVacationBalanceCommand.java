package com.performily.flowboard.benefits.domain.model.commands;

/**
 * Opens the vacation balance of an employee with the days earned by seniority.
 * It does nothing when the employee already has a balance.
 */
public record OpenVacationBalanceCommand(Long employeeId) {
}
