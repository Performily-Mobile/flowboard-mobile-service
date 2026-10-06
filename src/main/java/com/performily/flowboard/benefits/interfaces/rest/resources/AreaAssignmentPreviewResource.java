package com.performily.flowboard.benefits.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AreaAssignmentPreview", description = "What an assignment to an area would do, before confirming it")
public record AreaAssignmentPreviewResource(
        @Schema(example = "1") Long areaId,
        @Schema(description = "Active employees of the area", example = "48") int activeEmployees,
        @Schema(description = "Employees that already have the benefit in the period", example = "2") int alreadyAssigned,
        @Schema(description = "Employees that will receive it", example = "46") int toAssign
) {
}
