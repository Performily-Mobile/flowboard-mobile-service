package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Job Assignment Resource
 * @summary
 * Resource for one entry of the employee job history.
 *
 * @since 1.0.0
 */
@Schema(name = "JobAssignmentResponse", description = "Job assignment information response")
public record JobAssignmentResource(
        @Schema(description = "Job assignment identifier", example = "1") Long id,
        @Schema(description = "Area identifier", example = "1") Long areaId,
        @Schema(description = "Area name", example = "Recursos Humanos") String areaName,
        @Schema(description = "Position identifier", example = "1") Long positionId,
        @Schema(description = "Position title", example = "Analista de RR.HH.") String positionTitle,
        @Schema(description = "Change type", example = "HIRE") String changeType,
        @Schema(description = "Start date", example = "2026-01-05") LocalDate startDate,
        @Schema(description = "End date, null when current") LocalDate endDate,
        @Schema(description = "Whether this is the current assignment", example = "true") boolean current
) {
}