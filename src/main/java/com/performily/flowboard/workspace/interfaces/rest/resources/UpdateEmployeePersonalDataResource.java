package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Update Employee Personal Data Resource
 * @summary
 * Resource for updating the personal data of an employee.
 *
 * @since 1.0.0
 */
@Schema(name = "UpdateEmployeePersonalDataRequest", description = "Request payload for updating personal data")
public record UpdateEmployeePersonalDataResource(
        @NotBlank(message = "{validation.not-blank}") @Size(max = 50)
        @Schema(description = "First name", example = "María")
        String firstName,

        @NotBlank(message = "{validation.not-blank}") @Size(max = 80)
        @Schema(description = "Last name", example = "Quispe Rojas")
        String lastName,

        @NotNull(message = "{validation.not-null}") @Past
        @Schema(description = "Birth date", example = "1998-04-15")
        LocalDate birthDate,

        @NotBlank(message = "{validation.not-blank}") @Email(message = "{validation.email}")
        @Schema(description = "E-mail address", example = "maria.quispe@flowboard.pe")
        String email,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Phone number", example = "+51987654321")
        String phoneNumber,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Street and number", example = "Av. Primavera 123")
        String street,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "District", example = "Santiago de Surco")
        String district,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Province", example = "Lima")
        String province,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Department", example = "Lima")
        String department
) {
}