package com.performily.flowboard.payroll.interfaces.rest.transform;

import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.interfaces.rest.resources.PayslipResource;

import java.time.format.DateTimeFormatter;

public class PayslipResourceFromEntityAssembler {
    
    public static PayslipResource toResourceFromEntity(Payslip entity, String periodDisplay) {
        
        // a) Formatear la fecha a DD/MM
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");
        String formattedIssueDate = entity.getIssueDate().format(dateFormatter);
        
        // b) Formatear el monto a S/ #,###.00
        String formattedNetAmount = String.format("s/ %,.2f", entity.getNetAmount());
        
        return new PayslipResource(
                entity.getId(),
                periodDisplay,
                formattedIssueDate,
                formattedNetAmount,
                entity.getPublicationStatus().name(),
                entity.getPaymentStatus().name()
        );
    }
}