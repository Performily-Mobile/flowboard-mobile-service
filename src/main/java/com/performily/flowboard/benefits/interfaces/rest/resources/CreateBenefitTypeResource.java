package com.performily.flowboard.benefits.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "CreateBenefitTypeRequest", description = "New benefit type of the catalog (US37)")
public record CreateBenefitTypeResource(
        @NotBlank(message = "{validation.not-blank}") @Size(max = 80)
        @Schema(description = "Unique name", example = "Bono de productividad")
        String name,

        @Size(max = 250)
        @Schema(description = "Optional description", example = "Bono trimestral por cumplimiento de metas")
        String description,

        @Schema(description = "Whether the benefit keeps a balance", example = "false", defaultValue = "false")
        Boolean hasBalance,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Unit of measure", example = "MONEY", allowableValues = {"MONEY", "DAYS", "UNITS"})
        String unit
) {
}
