package com.performily.flowboard.benefits.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BenefitType", description = "Benefit type of the catalog")
public record BenefitTypeResource(
        @Schema(example = "3") Long id,
        @Schema(example = "Vales de consumo") String name,
        @Schema(example = "Vales mensuales de consumo") String description,
        @Schema(example = "MONEY") String unit,
        @Schema(example = "false") boolean hasBalance,
        @Schema(example = "true") boolean active
) {
}
