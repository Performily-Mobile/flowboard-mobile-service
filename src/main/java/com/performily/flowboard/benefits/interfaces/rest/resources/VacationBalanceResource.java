package com.performily.flowboard.benefits.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(name = "VacationBalance", description = "Vacation balance of an employee (US41)")
public record VacationBalanceResource(
        @Schema(example = "7") Long employeeId,
        @Schema(description = "Taken from Workspace", example = "Rosa Espinoza Gil") String employeeName,
        @Schema(description = "Taken from Workspace", example = "Operaciones") String areaName,
        @Schema(example = "22.00") BigDecimal accruedDays,
        @Schema(example = "8.00") BigDecimal usedDays,
        @Schema(example = "14.00") BigDecimal availableDays,
        @Schema(example = "2026-10-01") LocalDate lastAccrualDate,
        @Schema(description = "Newest first. Empty in the list of balances") List<VacationMovementResource> movements
) {
}
