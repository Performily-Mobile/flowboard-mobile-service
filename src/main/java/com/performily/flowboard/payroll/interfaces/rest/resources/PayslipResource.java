package com.performily.flowboard.payroll.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Payslip Resource
 * @summary
 * Resource for a payslip. Values are raw (ISO dates, amounts and enum names);
 * the client formats them for display.
 *
 * @since 1.0.0
 */
@Schema(name = "PayslipResponse", description = "Payslip information response")
public record PayslipResource(
        @Schema(description = "Payslip unique identifier", example = "1")
        Long id,

        @Schema(description = "Employee identifier", example = "12")
        Long employeeId,

        @Schema(description = "Employee full name", example = "Luis Ramos Vega")
        String employeeName,

        @Schema(description = "Payroll period identifier", example = "3")
        Long payrollPeriodId,

        @Schema(description = "Pay period year", example = "2026")
        int periodYear,

        @Schema(description = "Pay period month (1-12)", example = "8")
        int periodMonth,

        @Schema(description = "Readable pay period", example = "Agosto 2026")
        String periodLabel,

        @Schema(description = "File name", example = "boleta-2026-08.pdf")
        String fileName,

        @Schema(description = "File content type", example = "application/pdf")
        String contentType,

        @Schema(description = "File size in bytes", example = "245760")
        long sizeInBytes,

        @Schema(description = "Issue date", example = "2026-08-31")
        LocalDate issueDate,

        @Schema(description = "Net amount", example = "3450.00")
        BigDecimal netAmount,

        @Schema(description = "Currency (ISO 4217)", example = "PEN")
        String currency,

        @Schema(description = "Publication status", example = "PUBLISHED", allowableValues = {"UNDER_REVIEW", "PUBLISHED"})
        String publicationStatus,

        @Schema(description = "When the payslip was published", example = "2026-09-01T09:30:00")
        LocalDateTime publishedAt,

        @Schema(description = "Payment status", example = "PAID", allowableValues = {"PENDING", "PAID", "OBSERVED"})
        String paymentStatus,

        @Schema(description = "Payment date, only when PAID", example = "2026-09-01")
        LocalDate paidOn,

        @Schema(description = "Observation reason, only when OBSERVED", example = "Cuenta bancaria inválida")
        String observationReason
) {
}
