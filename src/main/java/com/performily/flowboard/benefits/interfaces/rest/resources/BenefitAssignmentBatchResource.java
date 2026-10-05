package com.performily.flowboard.benefits.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "BenefitAssignmentBatch",
        description = "Result of an assignment: one item for an employee, one per employee for an area")
public record BenefitAssignmentBatchResource(
        @Schema(description = "Present when the benefit was assigned to an area", example = "1") Long areaId,
        @Schema(example = "46") int assignedCount,
        @Schema(description = "Employees skipped because they already had the benefit in the period", example = "2") int skippedCount,
        List<Long> skippedEmployeeIds,
        List<BenefitAssignmentResource> assignments
) {
}
