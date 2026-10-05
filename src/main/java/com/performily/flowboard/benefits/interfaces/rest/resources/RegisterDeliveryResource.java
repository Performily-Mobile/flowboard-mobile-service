package com.performily.flowboard.benefits.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(name = "RegisterDeliveryRequest", description = "Effective delivery of an assigned benefit (US39)")
public record RegisterDeliveryResource(
        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Delivery date, not in the future", example = "2026-10-04")
        LocalDate deliveredOn,

        @Positive
        @Schema(description = "HR employee that registers it. Optional until IAM exists", example = "1")
        Long registeredById,

        @Size(max = 250)
        @Schema(description = "Optional notes", example = "Entregado en recepción")
        String notes
) {
}
