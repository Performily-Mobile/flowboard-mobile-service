package com.performily.flowboard.payroll.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Upload Payslip Resource
 * @summary
 * Resource for uploading a payslip. The PDF is uploaded to the storage service
 * by the client; this request only registers its metadata.
 *
 * @since 1.0.0
 */
@Schema(name = "UploadPayslipRequest", description = "Request payload for uploading a payslip")
public record UploadPayslipResource(
        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "Employee identifier", example = "12")
        Long employeeId,

        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "Payroll period identifier", example = "3")
        Long payrollPeriodId,

        @NotBlank(message = "{validation.not-blank}") @Size(max = 255)
        @Schema(description = "File name", example = "boleta-2026-08.pdf")
        String fileName,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Content type. Only PDF is accepted", example = "application/pdf", allowableValues = {"application/pdf"})
        String contentType,

        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "File size in bytes, up to 5 MB", example = "245760")
        Long sizeInBytes,

        @NotBlank(message = "{validation.not-blank}") @Size(max = 500)
        @Schema(description = "Storage URL", example = "https://storage.example.com/boletas/boleta-2026-08.pdf")
        String storageUrl,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Issue date", example = "2026-08-31")
        LocalDate issueDate,

        @NotNull(message = "{validation.not-null}") @PositiveOrZero
        @Schema(description = "Net amount", example = "3450.00")
        BigDecimal netAmount,

        @Schema(description = "Currency (ISO 4217). Default PEN", example = "PEN")
        String currency
) {
}
