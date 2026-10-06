package com.performily.flowboard.benefits.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "BenefitAssignment", description = "A benefit assigned to an employee")
public record BenefitAssignmentResource(
        @Schema(example = "12") Long id,
        @Schema(example = "3") Long benefitTypeId,
        @Schema(example = "Vales de consumo") String benefitTypeName,
        @Schema(example = "MONEY") String unit,
        @Schema(example = "1") Long employeeId,
        @Schema(description = "Taken from Workspace", example = "María Quispe Rojas") String employeeName,
        @Schema(description = "Present when it was assigned to an area", example = "1") Long sourceAreaId,
        @Schema(example = "150.00") BigDecimal quantity,
        @Schema(description = "Quantity ready to show", example = "S/ 150.00") String displayQuantity,
        @Schema(example = "2026-10-01") LocalDate startDate,
        @Schema(example = "2026-10-31") LocalDate endDate,
        @Schema(example = "ASSIGNED", allowableValues = {"ASSIGNED", "DELIVERED", "CANCELLED"}) String status,
        @Schema(description = "Present when it was delivered") BenefitDeliveryResource delivery
) {
}
