package com.performily.flowboard.benefits.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "VacationMovement", description = "One change of a vacation balance")
public record VacationMovementResource(
        @Schema(example = "8") Long id,
        @Schema(example = "USAGE", allowableValues = {"ACCRUAL", "USAGE", "REVERSAL", "MANUAL_ADJUSTMENT"}) String type,
        @Schema(description = "Signed effect on the available days", example = "-3.00") BigDecimal days,
        @Schema(example = "Corrección de saldo 2025") String reason,
        @Schema(example = "1") Long authorId,
        @Schema(description = "Taken from Workspace", example = "Ana Martínez") String authorName,
        @Schema(description = "Request that originated it", example = "142") Long requestId,
        @Schema(example = "2026-08-12T09:30:00") LocalDateTime occurredAt
) {
}
