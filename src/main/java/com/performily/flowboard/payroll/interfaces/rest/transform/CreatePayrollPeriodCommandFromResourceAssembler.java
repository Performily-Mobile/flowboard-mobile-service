package com.performily.flowboard.payroll.interfaces.rest.transform;

import com.performily.flowboard.payroll.domain.model.commands.CreatePayrollPeriodCommand;
import com.performily.flowboard.payroll.interfaces.rest.resources.CreatePayrollPeriodResource;

/**
 * Create Payroll Period Command From Resource Assembler
 * @summary
 * Assembler to convert a CreatePayrollPeriodResource to a CreatePayrollPeriodCommand.
 *
 * @since 1.0.0
 */
public class CreatePayrollPeriodCommandFromResourceAssembler {
    /**
     * Converts a {@link CreatePayrollPeriodResource} to a {@link CreatePayrollPeriodCommand}.
     *
     * @param resource the {@link CreatePayrollPeriodResource} instance
     * @return the {@link CreatePayrollPeriodCommand}
     */
    public static CreatePayrollPeriodCommand toCommandFromResource(CreatePayrollPeriodResource resource) {
        return new CreatePayrollPeriodCommand(resource.year(), resource.month(), resource.scheduledPaymentDate());
    }
}
