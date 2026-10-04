package com.performily.flowboard.request.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Field Value Resource
 * @summary
 * Resource for the value of one field of the dynamic form. It is used in the
 * requests and in the responses.
 *
 * @since 1.0.0
 */
@Schema(name = "FieldValue", description = "Value of a field of the dynamic form")
public record FieldValueResource(
        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Field key", example = "reason")
        String key,

        @Size(max = 500)
        @Schema(description = "Value as text. DATE uses yyyy-MM-dd, TIME uses HH:mm, BOOLEAN uses true or false",
                example = "Viaje familiar", maxLength = 500)
        String value
) {
}
