package com.performily.flowboard.payroll.interfaces.rest.transform;

import com.performily.flowboard.payroll.domain.model.commands.MarkPayslipAsPaidCommand;
import com.performily.flowboard.payroll.interfaces.rest.resources.MarkPayslipAsPaidResource;

/**
 * Mark Payslip As Paid Command From Resource Assembler
 * @summary
 * Assembler to convert a MarkPayslipAsPaidResource to a MarkPayslipAsPaidCommand.
 *
 * @since 1.0.0
 */
public class MarkPayslipAsPaidCommandFromResourceAssembler {
    /**
     * Converts a {@link MarkPayslipAsPaidResource} to a {@link MarkPayslipAsPaidCommand}.
     *
     * @param payslipId the payslip id
     * @param resource  the {@link MarkPayslipAsPaidResource} instance
     * @return the {@link MarkPayslipAsPaidCommand}
     */
    public static MarkPayslipAsPaidCommand toCommandFromResource(Long payslipId, MarkPayslipAsPaidResource resource) {
        return new MarkPayslipAsPaidCommand(payslipId, resource.paidOn());
    }
}
