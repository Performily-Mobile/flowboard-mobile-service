package com.performily.flowboard.payroll.interfaces.rest.transform;

import com.performily.flowboard.payroll.domain.model.commands.ReplacePayslipFileCommand;
import com.performily.flowboard.payroll.interfaces.rest.resources.ReplacePayslipFileResource;

/**
 * Replace Payslip File Command From Resource Assembler
 * @summary
 * Assembler to convert a ReplacePayslipFileResource to a ReplacePayslipFileCommand.
 *
 * @since 1.0.0
 */
public class ReplacePayslipFileCommandFromResourceAssembler {
    /**
     * Converts a {@link ReplacePayslipFileResource} to a {@link ReplacePayslipFileCommand}.
     *
     * @param payslipId the payslip id
     * @param resource  the {@link ReplacePayslipFileResource} instance
     * @return the {@link ReplacePayslipFileCommand}
     */
    public static ReplacePayslipFileCommand toCommandFromResource(Long payslipId, ReplacePayslipFileResource resource) {
        return new ReplacePayslipFileCommand(payslipId, resource.fileName(), resource.contentType(),
                resource.sizeInBytes(), resource.storageUrl(), resource.issueDate(), resource.netAmount(),
                resource.currency());
    }
}
