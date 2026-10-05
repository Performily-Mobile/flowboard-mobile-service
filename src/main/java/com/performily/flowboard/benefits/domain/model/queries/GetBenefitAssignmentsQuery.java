package com.performily.flowboard.benefits.domain.model.queries;

import com.performily.flowboard.benefits.domain.model.valueobjects.AssignmentStatus;

/**
 * Assignments for HR staff, optionally filtered by status and benefit type
 * ("Por entregar" / "Entregados" tabs).
 */
public record GetBenefitAssignmentsQuery(AssignmentStatus status, Long benefitTypeId) {
}
