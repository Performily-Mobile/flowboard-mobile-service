package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Employee Resource
 * @summary
 * Resource for an employee.
 *
 * @since 1.0.0
 */
@Schema(name = "EmployeeResponse", description = "Employee information response")
public record EmployeeResource(
        @Schema(description = "Employee unique identifier", example = "1") Long id,
        @Schema(description = "First name", example = "María") String firstName,
        @Schema(description = "Last name", example = "Quispe Rojas") String lastName,
        @Schema(description = "Full name", example = "María Quispe Rojas") String fullName,
        @Schema(description = "Identity document type", example = "DNI") String identityDocumentType,
        @Schema(description = "Identity document number", example = "71234567") String identityDocumentNumber,
        @Schema(description = "Birth date", example = "1998-04-15") LocalDate birthDate,
        @Schema(description = "E-mail address", example = "maria.quispe@flowboard.pe") String email,
        @Schema(description = "Phone number", example = "+51987654321") String phoneNumber,
        @Schema(description = "Street and number", example = "Av. Primavera 123") String street,
        @Schema(description = "District", example = "Santiago de Surco") String district,
        @Schema(description = "Province", example = "Lima") String province,
        @Schema(description = "Department", example = "Lima") String department,
        @Schema(description = "Contract type", example = "INDEFINITE") String contractType,
        @Schema(description = "Hire date", example = "2026-01-05") LocalDate hireDate,
        @Schema(description = "Contract end date", example = "2026-12-31") LocalDate contractEndDate,
        @Schema(description = "Employment status", example = "ACTIVE") String status,
        @Schema(description = "Termination reason") String terminationReason,
        @Schema(description = "Termination date") LocalDate terminationDate,
        @Schema(description = "Area identifier", example = "1") Long areaId,
        @Schema(description = "Area name", example = "Recursos Humanos") String areaName,
        @Schema(description = "Position identifier", example = "1") Long positionId,
        @Schema(description = "Position title", example = "Analista de RR.HH.") String positionTitle,
        @Schema(description = "Direct manager identifier", example = "2") Long directManagerId,
        @Schema(description = "Date and time of the last modification") LocalDateTime updatedAt
) {
}