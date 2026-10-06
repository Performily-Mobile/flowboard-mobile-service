package com.performily.flowboard.payroll.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Payslip Detail Resource
 * @summary
 * Resource for the payslip viewer: the payslip plus the employer data.
 *
 * @since 1.0.0
 */
@Schema(name = "PayslipDetailResponse", description = "Payslip detail response")
public record PayslipDetailResource(
        @Schema(description = "Payslip data")
        PayslipResource payslip,

        @Schema(description = "Employer legal name", example = "EMPRESA S.A.C.")
        String companyLegalName,

        @Schema(description = "Employer tax id (RUC)", example = "20123456789")
        String companyTaxId
) {
}
