package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Create Area Resource
 * @summary
 * Resource for creating an area.
 *
 * @since 1.0.0
 */
@Schema(name = "CreateAreaRequest", description = "Request payload for creating a new area")
public record CreateAreaResource(
        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 80)
        @Schema(description = "Area name", example = "Recursos Humanos", maxLength = 80)
        String name,

        @Size(max = 255)
        @Schema(description = "Area description", example = "Gestión del talento y planillas", maxLength = 255)
        String description
) {
}