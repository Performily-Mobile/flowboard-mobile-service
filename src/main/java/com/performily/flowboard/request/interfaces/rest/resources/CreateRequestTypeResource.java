package com.performily.flowboard.request.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Create Request Type Resource
 * @summary
 * Resource for creating a request type, optionally with the fields of its form.
 *
 * @since 1.0.0
 */
@Schema(name = "CreateRequestTypeRequest", description = "Request payload for creating a request type")
public record CreateRequestTypeResource(
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
        String balanceDeduction,

        @Valid
        @Schema(description = "Fields of the form")
        List<CreateRequestFieldResource> fields
) {
}
