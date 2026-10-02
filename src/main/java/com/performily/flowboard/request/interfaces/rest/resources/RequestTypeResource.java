package com.performily.flowboard.request.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Request Type Resource
 * @summary
 * Resource for a request type with the fields of its form. The app uses it to
 * build the form without knowing the types in advance.
 *
 * @since 1.0.0
 */
@Schema(name = "RequestTypeResponse", description = "Request type information response")
public record RequestTypeResource(
        @Schema(description = "Request type identifier", example = "1") Long id,
        @Schema(description = "Name", example = "Vacaciones") String name,
        @Schema(description = "Description", example = "Solicitud de días de descanso remunerado") String description,
        @Schema(description = "Whether an attachment is required", example = "false") boolean requiresAttachment,
        @Schema(description = "Balance deducted when approved", example = "VACATION_DAYS") String balanceDeduction,
        @Schema(description = "Whether the type is active", example = "true") boolean active,
        @Schema(description = "Fields of the form, ordered by display order") List<RequestFieldResource> fields
) {
}
