package com.performily.flowboard.payroll.domain.model.valueobjects;

import java.time.LocalDateTime;

/**
 * Payslip Download Link
 * @summary
 * Temporary link to download a payslip file.
 *
 * @param downloadUrl the temporary URL
 * @param fileName    the file name
 * @param contentType the content type
 * @param expiresAt   when the link stops working
 * @since 1.0.0
 */
public record PayslipDownloadLink(String downloadUrl, String fileName, String contentType, LocalDateTime expiresAt) {
    /**
     * Compact constructor for PayslipDownloadLink.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public PayslipDownloadLink {
        if (downloadUrl == null || downloadUrl.isBlank()) {
            throw new IllegalArgumentException("Download URL is required");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Expiration date is required");
        }
    }
}
