package com.performily.flowboard.payroll.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Mark Payslip As Paid Resource
 * @summary
 * Resource for marking the payment of a payslip as paid.
 *
 * @since 1.0.0
 */
@Schema(name = "MarkPayslipAsPaidRequest", description = "Request payload for marking a payment as paid")
public record MarkPayslipAsPaidResource(
        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Payment date", example = "2026-09-01")
        LocalDate paidOn
) {
}
