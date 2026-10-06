package com.performily.flowboard.payroll.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Payslip Download Url Resource
 * @summary
 * Resource with a temporary link to download a payslip.
 *
 * @since 1.0.0
 */
@Schema(name = "PayslipDownloadUrlResponse", description = "Temporary download link")
public record PayslipDownloadUrlResource(
        @Schema(description = "Temporary download URL")
        String downloadUrl,

        @Schema(description = "File name", example = "boleta-2026-08.pdf")
        String fileName,

        @Schema(description = "File content type", example = "application/pdf")
        String contentType,

        @Schema(description = "When the link stops working", example = "2026-09-01T09:35:00")
        LocalDateTime expiresAt
) {
}
