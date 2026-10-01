package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Position Resource
 * @summary
 * Resource for a position.
 *
 * @since 1.0.0
 */
@Schema(name = "PositionResponse", description = "Position information response")
public record PositionResource(
        @Schema(description = "Position unique identifier", example = "1")
        Long id,

        @Schema(description = "Position title", example = "Analista de RR.HH.")
        String title,

        @Schema(description = "Area identifier", example = "1")
        Long areaId,

        @Schema(description = "Area name", example = "Recursos Humanos")
        String areaName,

        @Schema(description = "Reference salary amount", example = "3500.00")
        BigDecimal referenceSalaryAmount,

        @Schema(description = "Reference salary currency (ISO 4217)", example = "PEN")
        String referenceSalaryCurrency,

        @Schema(description = "Whether the position is active", example = "true")
        boolean active
) {
}