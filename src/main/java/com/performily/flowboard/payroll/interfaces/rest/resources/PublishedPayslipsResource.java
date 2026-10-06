package com.performily.flowboard.payroll.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Published Payslips Resource
 * @summary
 * Resource with the number of payslips published at once.
 *
 * @since 1.0.0
 */
@Schema(name = "PublishedPayslipsResponse", description = "Number of payslips published")
public record PublishedPayslipsResource(
        @Schema(description = "Payroll period identifier", example = "3")
        Long payrollPeriodId,

        @Schema(description = "Number of payslips published", example = "248")
        int publishedCount
) {
}
