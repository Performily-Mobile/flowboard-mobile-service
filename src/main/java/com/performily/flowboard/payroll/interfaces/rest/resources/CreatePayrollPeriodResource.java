package com.performily.flowboard.payroll.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Create Payroll Period Resource
 * @summary
 * Resource for opening a payroll period.
 *
 * @since 1.0.0
 */
@Schema(name = "CreatePayrollPeriodRequest", description = "Request payload for creating a payroll period")
public record CreatePayrollPeriodResource(
        @NotNull(message = "{validation.not-null}") @Min(2000)
        @Schema(description = "Year", example = "2026")
        Integer year,

        @NotNull(message = "{validation.not-null}") @Min(1) @Max(12)
        @Schema(description = "Month (1-12)", example = "8")
        Integer month,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Scheduled payment date", example = "2026-09-01")
        LocalDate scheduledPaymentDate
) {
}
