package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Rename Area Resource
 * @summary
 * Resource for renaming an area.
 *
 * @since 1.0.0
 */
@Schema(name = "RenameAreaRequest", description = "Request payload for renaming an area")
public record RenameAreaResource(
        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 80)
        @Schema(description = "New area name", example = "Gestión Humana", maxLength = 80)
        String name
) {
}