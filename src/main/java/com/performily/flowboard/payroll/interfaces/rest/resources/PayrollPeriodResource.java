package com.performily.flowboard.payroll.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Payroll Period Resource
 * @summary
 * Resource for a payroll period.
 *
 * @since 1.0.0
 */
@Schema(name = "PayrollPeriodResponse", description = "Payroll period information response")
public record PayrollPeriodResource(
        @Schema(description = "Payroll period unique identifier", example = "3")
        Long id,

        @Schema(description = "Year", example = "2026")
        int year,

        @Schema(description = "Month (1-12)", example = "8")
        int month,

        @Schema(description = "Readable period", example = "Agosto 2026")
        String label,

        @Schema(description = "Scheduled payment date", example = "2026-09-01")
        LocalDate scheduledPaymentDate
) {
}
