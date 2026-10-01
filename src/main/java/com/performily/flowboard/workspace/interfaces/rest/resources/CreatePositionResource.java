package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Create Position Resource
 * @summary
 * Resource for creating a position.
 *
 * @since 1.0.0
 */
@Schema(name = "CreatePositionRequest", description = "Request payload for creating a new position")
public record CreatePositionResource(
        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 80)
        @Schema(description = "Position title", example = "Analista de RR.HH.", maxLength = 80)
        String title,

        @NotNull(message = "{validation.not-null}")
        @Positive
        @Schema(description = "Area identifier", example = "1")
        Long areaId,

        @NotNull(message = "{validation.not-null}")
        @PositiveOrZero
        @Schema(description = "Reference salary amount", example = "3500.00")
        BigDecimal referenceSalaryAmount,

        @Schema(description = "Reference salary currency (ISO 4217). Default PEN", example = "PEN")
        String referenceSalaryCurrency
) {
}