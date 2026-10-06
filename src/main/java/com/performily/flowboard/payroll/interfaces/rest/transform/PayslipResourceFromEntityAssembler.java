package com.performily.flowboard.payroll.interfaces.rest.transform;

import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.interfaces.rest.resources.PayslipResource;

/**
 * Payslip Resource From Entity Assembler
 * @summary
 * Assembler to convert a Payslip aggregate to a PayslipResource.
 *
 * @since 1.0.0
 */
public class PayslipResourceFromEntityAssembler {
    /**
     * Converts a {@link Payslip} to a {@link PayslipResource}.
     *
     * @param entity       the {@link Payslip} instance
     * @param employeeName the employee full name, taken from Workspace
     * @return the {@link PayslipResource}
     */
    public static PayslipResource toResourceFromEntity(Payslip entity, String employeeName) {
        var period = entity.getPayrollPeriod().getPeriod();
        var file = entity.getFile();
        var payment = entity.getPayment();
        return new PayslipResource(
                entity.getId(),
                entity.getEmployeeId().value(),
                employeeName,
                entity.getPayrollPeriod().getId(),
                period.year(),
                period.month(),
                period.label(),
                file.fileName(),
                file.contentType(),
                file.sizeInBytes(),
                entity.getIssueDate(),
                entity.getNetAmount().amount(),
                entity.getNetAmount().currency().getCurrencyCode(),
                entity.getPublicationStatus().name(),
                entity.getPublishedAt(),
                payment.status().name(),
                payment.paidOn(),
                payment.observationReason());
    }
}
