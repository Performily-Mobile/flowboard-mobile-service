package com.performily.flowboard.benefits.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(name = "AdjustVacationBalanceRequest", description = "Manual adjustment of a vacation balance (US42)")
public record AdjustVacationBalanceResource(
        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "ADD adds days, DEDUCT discounts them", example = "ADD", allowableValues = {"ADD", "DEDUCT"})
        String operation,

        @NotNull(message = "{validation.not-null}") @Positive @Digits(integer = 4, fraction = 2)
        @Schema(description = "Number of days", example = "1")
        BigDecimal days,

        @NotBlank(message = "{validation.not-blank}") @Size(max = 250)
        @Schema(description = "Reason of the adjustment", example = "Corrección de saldo 2025")
        String reason,

        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "HR employee that makes the adjustment. It will come from the token when IAM exists", example = "1")
        Long authorId
) {
}
