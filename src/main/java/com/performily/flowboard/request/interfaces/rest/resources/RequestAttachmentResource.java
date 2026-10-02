package com.performily.flowboard.request.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request Attachment Resource
 * @summary
 * Resource for an attachment of a request. The file is uploaded to the storage
 * service by the client; the API only registers its metadata (FileReference).
 * It is used in the requests and in the responses.
 *
 * @since 1.0.0
 */
@Schema(name = "RequestAttachment", description = "Metadata of a file attached to a request")
public record RequestAttachmentResource(
        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "File name", example = "descanso-medico.pdf")
        String fileName,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Content type", example = "application/pdf")
        String contentType,

        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "File size in bytes", example = "245760")
        Long sizeInBytes,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Storage URL", example = "https://storage.example.com/descanso-medico.pdf")
        String storageUrl
) {
}
