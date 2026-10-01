package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * Reinstate Employee Resource
 * @summary
 * Resource for reinstating a terminated employee.
 *
 * @since 1.0.0
 */
@Schema(name = "ReinstateEmployeeRequest", description = "Request payload for reinstating an employee")
public record ReinstateEmployeeResource(
        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "Area identifier", example = "1")
        Long areaId,

        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "Position identifier", example = "1")
        Long positionId,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Reinstatement date", example = "2027-03-01")
        LocalDate reinstatementDate
) {
}