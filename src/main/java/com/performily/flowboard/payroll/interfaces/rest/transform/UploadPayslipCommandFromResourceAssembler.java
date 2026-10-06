package com.performily.flowboard.payroll.interfaces.rest.transform;

import com.performily.flowboard.payroll.domain.model.commands.UploadPayslipCommand;
import com.performily.flowboard.payroll.interfaces.rest.resources.UploadPayslipResource;

/**
 * Upload Payslip Command From Resource Assembler
 * @summary
 * Assembler to convert an UploadPayslipResource to an UploadPayslipCommand.
 *
 * @since 1.0.0
 */
public class UploadPayslipCommandFromResourceAssembler {
    /**
     * Converts a {@link UploadPayslipResource} to a {@link UploadPayslipCommand}.
     *
     * @param resource the {@link UploadPayslipResource} instance
     * @return the {@link UploadPayslipCommand}
     */
    public static UploadPayslipCommand toCommandFromResource(UploadPayslipResource resource) {
        return new UploadPayslipCommand(resource.employeeId(), resource.payrollPeriodId(), resource.fileName(),
                resource.contentType(), resource.sizeInBytes(), resource.storageUrl(), resource.issueDate(),
                resource.netAmount(), resource.currency());
    }
}
