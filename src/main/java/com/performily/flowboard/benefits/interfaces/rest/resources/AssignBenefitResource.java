package com.performily.flowboard.benefits.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "AssignBenefitRequest",
        description = "Assignment of a benefit (US38). Send employeeId for one employee or areaId for every active employee of the area, not both")
public record AssignBenefitResource(
        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "Benefit type", example = "3")
        Long benefitTypeId,

        @Positive
        @Schema(description = "Employee that receives it. Use it or areaId", example = "1")
        Long employeeId,

        @Positive
        @Schema(description = "Area whose active employees receive it. Use it or employeeId", example = "1")
        Long areaId,

        @NotNull(message = "{validation.not-null}") @Positive @Digits(integer = 10, fraction = 2)
        @Schema(description = "Quantity in the unit of the type", example = "150.00")
        BigDecimal quantity,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "First day of the validity", example = "2026-10-01")
        LocalDate startDate,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Last day of the validity", example = "2026-10-31")
        LocalDate endDate
) {
}
