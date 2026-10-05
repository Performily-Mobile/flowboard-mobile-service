package com.performily.flowboard.benefits.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "EmployeeBenefits", description = "Benefits of one employee: current and delivered (US40)")
public record EmployeeBenefitsResource(
        @Schema(example = "1") Long employeeId,
        @Schema(example = "2") int currentCount,
        @Schema(example = "3") int deliveredCount,
        @Schema(description = "Assigned, not delivered and still valid") List<BenefitAssignmentResource> current,
        @Schema(description = "Already delivered, newest first") List<BenefitAssignmentResource> delivered
) {
}
