package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * Update Position Reference Salary Resource
 * @summary
 * Resource for updating the reference salary of a position.
 *
 * @since 1.0.0
 */
@Schema(name = "UpdatePositionReferenceSalaryRequest", description = "Request payload for updating the reference salary")
public record UpdatePositionReferenceSalaryResource(
        @NotNull(message = "{validation.not-null}")
        @PositiveOrZero
        @Schema(description = "Reference salary amount", example = "3800.00")
        BigDecimal referenceSalaryAmount,

        @Schema(description = "Reference salary currency (ISO 4217). Default PEN", example = "PEN")
        String referenceSalaryCurrency
) {
}