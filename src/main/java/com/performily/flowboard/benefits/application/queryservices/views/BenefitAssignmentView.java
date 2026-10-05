package com.performily.flowboard.benefits.application.queryservices.views;

import com.performily.flowboard.benefits.domain.model.aggregates.BenefitAssignment;

/**
 * An assignment with the name of its employee, taken from Workspace at query time
 * (the name is never stored in Benefits).
 */
public record BenefitAssignmentView(BenefitAssignment assignment, String employeeName) {
}
