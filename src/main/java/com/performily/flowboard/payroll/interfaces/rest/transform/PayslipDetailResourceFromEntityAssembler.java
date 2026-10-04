package com.performily.flowboard.payroll.interfaces.rest.transform;

import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.interfaces.rest.resources.PayslipDetailResource;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PayslipDetailResourceFromEntityAssembler {

    public static PayslipDetailResource toResourceFromEntity(
            Payslip entity, 
            String employeeFullName, 
            String companyInfo) {

        // a) y f) Formateo de meses (ej. "agosto 2026" y "Agosto 2026")
        Locale peruvianSpanish = Locale.forLanguageTag("es-PE");
        DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", peruvianSpanish);
        String rawMonthYear = entity.getIssueDate().format(monthYearFormatter);
        String headerTitle = rawMonthYear.toLowerCase();
        String period = rawMonthYear.substring(0, 1).toUpperCase() + rawMonthYear.substring(1);

        // b) Emitido el 31/08/2026
        DateTimeFormatter exactDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String issuedDateText = "Emitida el " + entity.getIssueDate().format(exactDateFormatter);

        // c) Traducción del estado de pago
        String paymentStatus = switch (entity.getPaymentStatus().name()) {
            case "PAID" -> "Pagado";
            case "PENDING" -> "Pendiente";
            case "OBSERVED" -> "Observado";
            default -> entity.getPaymentStatus().name();
        };

        // g) y h) Formato de moneda estricto: S/ #,###.00
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        
        String totalIncomes = "S/ " + df.format(entity.getGrossSalary());
        String totalDeductions = "S/ " + df.format(entity.getDeductions());
        String netAmount = "S/ " + df.format(entity.getNetAmount());

        return new PayslipDetailResource(
                entity.getId(),
                headerTitle,
                issuedDateText,
                paymentStatus,
                companyInfo,
                employeeFullName, // e) Nombre concatenado que provendrá del Facade
                period,
                totalIncomes,
                totalDeductions,
                netAmount
        );
    }
}