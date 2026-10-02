package com.performily.flowboard.request.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Create Request Field Resource
 * @summary
 * Resource for adding a field to the form of a request type.
 *
 * @since 1.0.0
 */
@Schema(name = "CreateRequestFieldRequest", description = "Request payload for adding a field to a request type")
public record CreateRequestFieldResource(
        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 50)
        @Schema(description = "Field key in camelCase, unique inside the type", example = "reason", maxLength = 50)
        String key,

        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 80)
        @Schema(description = "Label shown in the form", example = "Motivo", maxLength = 80)
        String label,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Data type of the field", example = "TEXT",
                allowableValues = {"TEXT", "NUMBER", "DATE", "TIME", "MONEY", "BOOLEAN"})
        String dataType,

        @Schema(description = "Whether the field is required", example = "true", defaultValue = "false")
        Boolean required,

        @PositiveOrZero
        @Schema(description = "Order of the field in the form", example = "1", defaultValue = "0")
        Integer displayOrder
) {
}
