package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Assign Direct Manager Resource
 * @summary
 * Resource for assigning the direct manager of an employee.
 *
 * @since 1.0.0
 */
@Schema(name = "AssignDirectManagerRequest", description = "Request payload for assigning a direct manager")
public record AssignDirectManagerResource(
        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "Direct manager employee identifier", example = "2")
        Long managerId
) {
}