package com.performily.flowboard.payroll.interfaces.rest.transform;

import com.performily.flowboard.payroll.domain.model.commands.MarkPayslipAsObservedCommand;
import com.performily.flowboard.payroll.interfaces.rest.resources.MarkPayslipAsObservedResource;

/**
 * Mark Payslip As Observed Command From Resource Assembler
 * @summary
 * Assembler to convert a MarkPayslipAsObservedResource to a MarkPayslipAsObservedCommand.
 *
 * @since 1.0.0
 */
public class MarkPayslipAsObservedCommandFromResourceAssembler {
    /**
     * Converts a {@link MarkPayslipAsObservedResource} to a {@link MarkPayslipAsObservedCommand}.
     *
     * @param payslipId the payslip id
     * @param resource  the {@link MarkPayslipAsObservedResource} instance
     * @return the {@link MarkPayslipAsObservedCommand}
     */
    public static MarkPayslipAsObservedCommand toCommandFromResource(Long payslipId, MarkPayslipAsObservedResource resource) {
        return new MarkPayslipAsObservedCommand(payslipId, resource.reason());
    }
}
