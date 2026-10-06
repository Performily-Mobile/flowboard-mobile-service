package com.performily.flowboard.payroll.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Mark Payslip As Observed Resource
 * @summary
 * Resource for marking the payment of a payslip as observed.
 *
 * @since 1.0.0
 */
@Schema(name = "MarkPayslipAsObservedRequest", description = "Request payload for marking a payment as observed")
public record MarkPayslipAsObservedResource(
        @NotBlank(message = "{validation.not-blank}") @Size(max = 500)
        @Schema(description = "Observation reason", example = "Cuenta bancaria inválida", maxLength = 500)
        String reason
) {
}
