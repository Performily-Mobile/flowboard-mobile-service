package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * Assign Job Resource
 * @summary
 * Resource for changing the area and position of an employee.
 *
 * @since 1.0.0
 */
@Schema(name = "AssignJobRequest", description = "Request payload for an area or position change")
public record AssignJobResource(
        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "New area identifier", example = "2")
        Long areaId,

        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "New position identifier", example = "4")
        Long positionId,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Date the change takes effect", example = "2026-10-01")
        LocalDate effectiveDate
) {
}