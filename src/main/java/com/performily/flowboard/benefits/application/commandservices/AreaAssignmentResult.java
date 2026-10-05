package com.performily.flowboard.benefits.application.commandservices;

import com.performily.flowboard.benefits.domain.model.aggregates.BenefitAssignment;

import java.util.List;

/**
 * Result of assigning a benefit to an area: the created assignments and the
 * employees that were skipped because they already had the benefit in the period.
 */
public record AreaAssignmentResult(Long areaId, List<BenefitAssignment> assignments, List<Long> skippedEmployeeIds) {
}
