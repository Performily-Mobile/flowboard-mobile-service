package com.performily.flowboard.benefits.domain.model.commands;

/**
 * Cancels an assignment that was not delivered.
 */
public record CancelBenefitAssignmentCommand(Long assignmentId) {
}
