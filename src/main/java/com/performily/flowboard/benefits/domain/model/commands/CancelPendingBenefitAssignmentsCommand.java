package com.performily.flowboard.benefits.domain.model.commands;

/**
 * Cancels every benefit not delivered yet of an employee. Sent when the
 * employee is terminated in Workspace.
 */
public record CancelPendingBenefitAssignmentsCommand(Long employeeId) {
}
