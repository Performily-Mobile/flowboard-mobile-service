package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Terminate Employee Resource
 * @summary
 * Resource for terminating an employee.
 *
 * @since 1.0.0
 */
@Schema(name = "TerminateEmployeeRequest", description = "Request payload for terminating an employee")
public record TerminateEmployeeResource(
        @NotBlank(message = "{validation.not-blank}") @Size(max = 500)
        @Schema(description = "Termination reason", example = "Renuncia voluntaria")
        String reason,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Termination date", example = "2026-10-31")
        LocalDate terminationDate
) {
}