package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Area Resource
 * @summary
 * Resource for an area.
 *
 * @since 1.0.0
 */
@Schema(name = "AreaResponse", description = "Area information response")
public record AreaResource(
        @Schema(description = "Area unique identifier", example = "1")
        Long id,

        @Schema(description = "Area name", example = "Recursos Humanos")
        String name,

        @Schema(description = "Area description", example = "Gestión del talento y planillas")
        String description,

        @Schema(description = "Whether the area is active", example = "true")
        boolean active
) {
}