package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Register Employee Resource
 * @summary
 * Resource for registering an employee.
 *
 * @since 1.0.0
 */
@Schema(name = "RegisterEmployeeRequest", description = "Request payload for registering a new employee")
public record RegisterEmployeeResource(
        @NotBlank(message = "{validation.not-blank}") @Size(max = 50)
        @Schema(description = "First name", example = "María")
        String firstName,

        @NotBlank(message = "{validation.not-blank}") @Size(max = 80)
        @Schema(description = "Last name", example = "Quispe Rojas")
        String lastName,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Identity document type", example = "DNI", allowableValues = {"DNI", "CE", "PASSPORT"})
        String identityDocumentType,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Identity document number", example = "71234567")
        String identityDocumentNumber,

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
        String department,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Contract type", example = "INDEFINITE",
                allowableValues = {"INDEFINITE", "FIXED_TERM", "PART_TIME", "INTERNSHIP"})
        String contractType,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Hire date", example = "2026-01-05")
        LocalDate hireDate,

        @Schema(description = "Contract end date. Required for FIXED_TERM", example = "2026-12-31")
        LocalDate contractEndDate,

        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "Area identifier", example = "1")
        Long areaId,

        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "Position identifier", example = "1")
        Long positionId
) {
}