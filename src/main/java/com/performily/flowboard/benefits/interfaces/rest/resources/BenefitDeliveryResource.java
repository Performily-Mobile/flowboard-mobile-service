package com.performily.flowboard.benefits.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "BenefitDelivery", description = "Delivery of an assigned benefit")
public record BenefitDeliveryResource(
        @Schema(example = "2026-10-04") LocalDate deliveredOn,
        @Schema(example = "1") Long registeredById,
        @Schema(example = "Entregado en recepción") String notes
) {
}
