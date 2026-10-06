package com.performily.flowboard.payroll.interfaces.rest.transform;

import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.interfaces.rest.resources.PayslipDetailResource;

/**
 * Payslip Detail Resource From Entity Assembler
 * @summary
 * Assembler to convert a Payslip aggregate to a PayslipDetailResource.
 *
 * @since 1.0.0
 */
public class PayslipDetailResourceFromEntityAssembler {
    /**
     * Converts a {@link Payslip} to a {@link PayslipDetailResource}.
     *
     * @param entity           the {@link Payslip} instance
     * @param employeeName     the employee full name, taken from Workspace
     * @param companyLegalName the employer legal name
     * @param companyTaxId     the employer tax id
     * @return the {@link PayslipDetailResource}
     */
    public static PayslipDetailResource toResourceFromEntity(Payslip entity, String employeeName,
                                                             String companyLegalName, String companyTaxId) {
        return new PayslipDetailResource(
                PayslipResourceFromEntityAssembler.toResourceFromEntity(entity, employeeName),
                companyLegalName,
                companyTaxId);
    }
}
