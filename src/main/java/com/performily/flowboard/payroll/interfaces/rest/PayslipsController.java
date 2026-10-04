package com.performily.flowboard.payroll.interfaces.rest;

import com.performily.flowboard.payroll.domain.model.queries.GetPayslipByIdAndEmployeeIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipDownloadUrlQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipsByEmployeeIdQuery;
import com.performily.flowboard.payroll.domain.services.PayrollQueryService;
import com.performily.flowboard.payroll.interfaces.rest.resources.PayslipDetailResource;
import com.performily.flowboard.payroll.interfaces.rest.resources.PayslipDownloadUrlResource;
import com.performily.flowboard.payroll.interfaces.rest.resources.PayslipResource;
import com.performily.flowboard.payroll.interfaces.rest.transform.PayslipDetailResourceFromEntityAssembler;
import com.performily.flowboard.payroll.interfaces.rest.transform.PayslipResourceFromEntityAssembler;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/payroll")
@Tag(name = "Payroll", description = "API para la gestión de boletas de pago") 
public class PayslipsController {

    private final PayrollQueryService payrollQueryService;

    @Value("${organization.legal-name:Flowboard Corp}")
    private String legalName;

    @Value("${organization.tax-id:20123456789}")
    private String taxId;

    public PayslipsController(PayrollQueryService payrollQueryService) {
        this.payrollQueryService = payrollQueryService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<PayslipResource>> getMyPayslips(
            @RequestParam(name = "year", required = false) Integer year,
            @RequestHeader("X-Employee-Id") Long currentEmployeeId) { // Reemplazo de Authentication
        
        int filterYear = (year != null) ? year : LocalDate.now().getYear();

        var query = new GetPayslipsByEmployeeIdQuery(currentEmployeeId, filterYear);
        var payslips = payrollQueryService.handle(query);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es-PE"));

        List<PayslipResource> resources = payslips.stream()
            .<PayslipResource>map(payslip -> {
                String formattedDate = payslip.getIssueDate().format(formatter);
                String periodDisplay = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
                return PayslipResourceFromEntityAssembler.toResourceFromEntity(payslip, periodDisplay);
            })
                .toList();

        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayslipDetailResource> getPayslipById(
            @PathVariable("id") Long id,
            @RequestHeader("X-Employee-Id") Long currentEmployeeId) { // Reemplazo de Authentication

        var query = new GetPayslipByIdAndEmployeeIdQuery(id, currentEmployeeId);
        var payslip = payrollQueryService.handle(query)
                .orElseThrow(() -> new RuntimeException("Boleta no encontrada o acceso denegado"));

        String employeeFullName = "Luis Ramos Vega";
        String companyInfo = legalName + " · RUC " + taxId;

        var resource = PayslipDetailResourceFromEntityAssembler
                .toResourceFromEntity(payslip, employeeFullName, companyInfo);

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/{id}/download-url")
    public ResponseEntity<PayslipDownloadUrlResource> getPayslipDownloadUrl(
            @PathVariable("id") Long id,
            @RequestHeader("X-Employee-Id") Long currentEmployeeId) { // Reemplazo de Authentication

        var query = new GetPayslipDownloadUrlQuery(id, currentEmployeeId);
        var dto = payrollQueryService.handle(query);

        var resource = new PayslipDownloadUrlResource(dto.downloadUrl(), dto.fileName(), dto.contentType());

        return ResponseEntity.ok(resource);
    }
}