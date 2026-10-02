package com.performily.flowboard.request.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Update Request Type Resource
 * @summary
 * Resource for updating the general data of a request type. The fields are
 * managed with their own endpoints.
 *
 * @since 1.0.0
 */
@Schema(name = "UpdateRequestTypeRequest", description = "Request payload for updating a request type")
public record UpdateRequestTypeResource(
        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 80)
        @Schema(description = "Request type name", example = "Vacaciones", maxLength = 80)
        String name,

        @Size(max = 255)
        @Schema(description = "Request type description", example = "Solicitud de días de descanso remunerado", maxLength = 255)
        String description,

        @Schema(description = "Whether an attachment is required", example = "false", defaultValue = "false")
        Boolean requiresAttachment,

        @Schema(description = "Balance deducted when the request is approved", example = "VACATION_DAYS",
                allowableValues = {"NONE", "VACATION_DAYS", "BENEFIT_BALANCE"}, defaultValue = "NONE")
        String balanceDeduction
) {
}
