package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Attach Employee Document Resource
 * @summary
 * Resource for attaching a document to an employee record.
 *
 * The file is uploaded to the storage service by the client; this request only
 * registers its metadata (FileReference).
 *
 * @since 1.0.0
 */
@Schema(name = "AttachEmployeeDocumentRequest", description = "Request payload for attaching a document")
public record AttachEmployeeDocumentResource(
        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Document type", example = "EMPLOYMENT_CONTRACT")
        String documentType,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "File name", example = "contrato.pdf")
        String fileName,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Content type", example = "application/pdf", allowableValues = {"application/pdf", "image/jpeg", "image/png"})
        String contentType,

        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "File size in bytes", example = "245760")
        Long sizeInBytes,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Storage URL", example = "https://storage.example.com/contrato.pdf")
        String storageUrl
) {
}