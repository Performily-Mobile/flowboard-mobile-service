package com.performily.flowboard.request.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request Field Resource
 * @summary
 * Resource for a field of the form of a request type.
 *
 * @since 1.0.0
 */
@Schema(name = "RequestFieldResponse", description = "Request field information response")
public record RequestFieldResource(
        @Schema(description = "Field identifier", example = "1") Long id,
        @Schema(description = "Field key", example = "reason") String key,
        @Schema(description = "Label shown in the form", example = "Motivo") String label,
        @Schema(description = "Data type", example = "TEXT") String dataType,
        @Schema(description = "Whether the field is required", example = "true") boolean required,
        @Schema(description = "Order in the form", example = "1") int displayOrder
) {
}
