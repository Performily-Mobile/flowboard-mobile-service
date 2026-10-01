package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Employee Document Resource
 * @summary
 * Resource for a document attached to an employee record.
 *
 * @since 1.0.0
 */
@Schema(name = "EmployeeDocumentResponse", description = "Employee document information response")
public record EmployeeDocumentResource(
        @Schema(description = "Document identifier", example = "1") Long id,
        @Schema(description = "Document type", example = "EMPLOYMENT_CONTRACT") String documentType,
        @Schema(description = "File name", example = "contrato.pdf") String fileName,
        @Schema(description = "Content type", example = "application/pdf") String contentType,
        @Schema(description = "File size in bytes", example = "245760") long sizeInBytes,
        @Schema(description = "Storage URL", example = "https://storage.example.com/contrato.pdf") String storageUrl,
        @Schema(description = "Upload date and time") LocalDateTime uploadedAt
) {
}