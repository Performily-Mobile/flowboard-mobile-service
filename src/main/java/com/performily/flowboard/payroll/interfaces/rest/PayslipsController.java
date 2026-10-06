package com.performily.flowboard.payroll.interfaces.rest;

import com.performily.flowboard.payroll.application.commandservices.PayslipCommandService;
import com.performily.flowboard.payroll.application.internal.outboundservices.acl.ExternalWorkspaceService;
import com.performily.flowboard.payroll.application.queryservices.PayslipQueryService;
import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.domain.model.commands.PublishPayslipCommand;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipByIdAndEmployeeIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipByIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipDownloadUrlQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipsByEmployeeIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipsByPayrollPeriodIdQuery;
import com.performily.flowboard.payroll.interfaces.rest.resources.MarkPayslipAsObservedResource;
import com.performily.flowboard.payroll.interfaces.rest.resources.MarkPayslipAsPaidResource;
import com.performily.flowboard.payroll.interfaces.rest.resources.PayslipDetailResource;
import com.performily.flowboard.payroll.interfaces.rest.resources.PayslipDownloadUrlResource;
import com.performily.flowboard.payroll.interfaces.rest.resources.PayslipResource;
import com.performily.flowboard.payroll.interfaces.rest.resources.ReplacePayslipFileResource;
import com.performily.flowboard.payroll.interfaces.rest.resources.UploadPayslipResource;
import com.performily.flowboard.payroll.interfaces.rest.transform.MarkPayslipAsObservedCommandFromResourceAssembler;
import com.performily.flowboard.payroll.interfaces.rest.transform.MarkPayslipAsPaidCommandFromResourceAssembler;
import com.performily.flowboard.payroll.interfaces.rest.transform.PayslipDetailResourceFromEntityAssembler;
import com.performily.flowboard.payroll.interfaces.rest.transform.PayslipDownloadUrlResourceFromValueObjectAssembler;
import com.performily.flowboard.payroll.interfaces.rest.transform.PayslipResourceFromEntityAssembler;
import com.performily.flowboard.payroll.interfaces.rest.transform.ReplacePayslipFileCommandFromResourceAssembler;
import com.performily.flowboard.payroll.interfaces.rest.transform.UploadPayslipCommandFromResourceAssembler;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Payslips Controller
 * @summary
 * REST controller that exposes the payslips.
 *
 * - /me endpoints: the employee's own payslips (only PUBLISHED ones).
 * - The other endpoints: HR management (upload, replace, publish and payment status).
 *
 * TEMPORARY: until IAM exists, the employee that asks is sent in the
 * X-Employee-Id header. When IAM is ready, it is taken from the token.
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/payslips", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Payslips", description = "Payslip repository and payment status endpoints")
public class PayslipsController {
    private static final String EMPLOYEE_HEADER = "X-Employee-Id";

    private final PayslipCommandService payslipCommandService;
    private final PayslipQueryService payslipQueryService;
    private final ExternalWorkspaceService externalWorkspaceService;
    private final String companyLegalName;
    private final String companyTaxId;

    /**
     * Constructor.
     *
     * @param payslipCommandService    the {@link PayslipCommandService} instance
     * @param payslipQueryService      the {@link PayslipQueryService} instance
     * @param externalWorkspaceService the {@link ExternalWorkspaceService} instance, used for the employee names
     * @param companyLegalName         the employer legal name
     * @param companyTaxId             the employer tax id
     */
    public PayslipsController(PayslipCommandService payslipCommandService,
                              PayslipQueryService payslipQueryService,
                              ExternalWorkspaceService externalWorkspaceService,
                              @Value("${organization.legal-name:Flowboard Corp}") String companyLegalName,
                              @Value("${organization.tax-id:20123456789}") String companyTaxId) {
        this.payslipCommandService = payslipCommandService;
        this.payslipQueryService = payslipQueryService;
        this.externalWorkspaceService = externalWorkspaceService;
        this.companyLegalName = companyLegalName;
        this.companyTaxId = companyTaxId;
    }

    // ---------------------------------------------------------------------
    // Employee: my payslips
    // ---------------------------------------------------------------------

    /**
     * Get my payslips.
     *
     * @param employeeId the employee that asks
     * @param year       the pay period year, the current year by default
     * @return the HTTP response
     */
    @GetMapping("/me")
    @Operation(summary = "Get my payslips",
            description = "Retrieves the PUBLISHED payslips of the employee for a year, the most recent period first.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payslips retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PayslipResource.class))),
            @ApiResponse(responseCode = "400", description = "Missing or invalid employee header")
    })
    public ResponseEntity<List<PayslipResource>> getMyPayslips(
            @RequestHeader(EMPLOYEE_HEADER) @Parameter(description = "Employee that asks (temporary until IAM)", example = "1") Long employeeId,
            @RequestParam(required = false) @Parameter(description = "Pay period year", example = "2026") Integer year) {
        var filterYear = year != null ? year : LocalDate.now().getYear();
        var payslips = payslipQueryService.handle(new GetPayslipsByEmployeeIdQuery(employeeId, filterYear));
        var employeeName = externalWorkspaceService.fetchEmployeeFullName(employeeId);
        var resources = payslips.stream()
                .map(payslip -> PayslipResourceFromEntityAssembler.toResourceFromEntity(payslip, employeeName))
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Get one of my payslips.
     *
     * @param employeeId the employee that asks
     * @param payslipId  the payslip id
     * @return the HTTP response
     */
    @GetMapping("/me/{payslipId}")
    @Operation(summary = "Get one of my payslips",
            description = "Retrieves a PUBLISHED payslip of the employee with the employer data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payslip found",
                    content = @Content(schema = @Schema(implementation = PayslipDetailResource.class))),
            @ApiResponse(responseCode = "404", description = "Payslip not found, not published or not owned by the employee")
    })
    public ResponseEntity<?> getMyPayslip(
            @RequestHeader(EMPLOYEE_HEADER) Long employeeId,
            @PathVariable @Parameter(description = "Payslip unique identifier", example = "1") Long payslipId) {
        var payslip = payslipQueryService.handle(new GetPayslipByIdAndEmployeeIdQuery(payslipId, employeeId));
        if (payslip.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    ApplicationError.notFound("Payslip", payslipId.toString()));
        }
        return ResponseEntity.ok(PayslipDetailResourceFromEntityAssembler.toResourceFromEntity(
                payslip.get(), externalWorkspaceService.fetchEmployeeFullName(employeeId),
                companyLegalName, companyTaxId));
    }

    /**
     * Get the download URL of one of my payslips.
     *
     * @param employeeId the employee that asks
     * @param payslipId  the payslip id
     * @return the HTTP response
     */
    @GetMapping("/me/{payslipId}/download-url")
    @Operation(summary = "Get the download URL of one of my payslips",
            description = "Returns a temporary link to download the PDF and records the download for audit.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Download link generated",
                    content = @Content(schema = @Schema(implementation = PayslipDownloadUrlResource.class))),
            @ApiResponse(responseCode = "404", description = "Payslip not found, not published or not owned by the employee")
    })
    public ResponseEntity<?> getMyPayslipDownloadUrl(
            @RequestHeader(EMPLOYEE_HEADER) Long employeeId,
            @PathVariable Long payslipId) {
        var result = payslipQueryService.handle(new GetPayslipDownloadUrlQuery(payslipId, employeeId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, PayslipDownloadUrlResourceFromValueObjectAssembler::toResourceFromValueObject, HttpStatus.OK);
    }

    // ---------------------------------------------------------------------
    // HR: payslip management
    // ---------------------------------------------------------------------

    /**
     * Get the payslips of a payroll period.
     *
     * @param payrollPeriodId the payroll period id
     * @return the HTTP response
     */
    @GetMapping
    @Operation(summary = "Get the payslips of a payroll period",
            description = "Retrieves every payslip of a payroll period, in any status (HR view).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payslips retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PayslipResource.class)))
    })
    public ResponseEntity<List<PayslipResource>> getPayslipsByPayrollPeriod(
            @RequestParam @Parameter(description = "Payroll period identifier", example = "3", required = true) Long payrollPeriodId) {
        var payslips = payslipQueryService.handle(new GetPayslipsByPayrollPeriodIdQuery(payrollPeriodId));
        return ResponseEntity.ok(payslips.stream().map(this::toResource).toList());
    }

    /**
     * Get payslip by ID.
     *
     * @param payslipId the payslip id
     * @return the HTTP response
     */
    @GetMapping("/{payslipId}")
    @Operation(summary = "Get payslip by ID", description = "Retrieves any payslip by its identifier (HR view).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payslip found",
                    content = @Content(schema = @Schema(implementation = PayslipResource.class))),
            @ApiResponse(responseCode = "404", description = "Payslip not found")
    })
    public ResponseEntity<?> getPayslipById(@PathVariable Long payslipId) {
        var payslip = payslipQueryService.handle(new GetPayslipByIdQuery(payslipId));
        if (payslip.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    ApplicationError.notFound("Payslip", payslipId.toString()));
        }
        return ResponseEntity.ok(toResource(payslip.get()));
    }

    /**
     * Upload a payslip.
     *
     * @param resource the {@link UploadPayslipResource} instance
     * @return the HTTP response
     */
    @PostMapping
    @Operation(summary = "Upload a payslip",
            description = "Registers the PDF of an employee for a payroll period. It starts UNDER_REVIEW and PENDING.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payslip uploaded successfully",
                    content = @Content(schema = @Schema(implementation = PayslipResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data (only PDF up to 5 MB)"),
            @ApiResponse(responseCode = "404", description = "Employee or payroll period not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - the employee already has a payslip for this period")
    })
    public ResponseEntity<?> uploadPayslip(@Valid @RequestBody UploadPayslipResource resource) {
        var command = UploadPayslipCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = payslipCommandService.handle(command)
                .flatMap(payslipId -> payslipQueryService.handle(new GetPayslipByIdQuery(payslipId))
                        .<Result<Payslip, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Payslip", payslipId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, this::toResource, HttpStatus.CREATED);
    }

    /**
     * Replace the file of a payslip.
     *
     * @param payslipId the payslip id
     * @param resource  the {@link ReplacePayslipFileResource} instance
     * @return the HTTP response
     */
    @PutMapping("/{payslipId}/file")
    @Operation(summary = "Replace the file of a payslip",
            description = "Replaces the PDF, issue date and net amount. The payslip goes back to UNDER_REVIEW and PENDING.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payslip file replaced",
                    content = @Content(schema = @Schema(implementation = PayslipResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Payslip not found"),
            @ApiResponse(responseCode = "422", description = "A paid payslip cannot be replaced")
    })
    public ResponseEntity<?> replacePayslipFile(@PathVariable Long payslipId,
                                                @Valid @RequestBody ReplacePayslipFileResource resource) {
        var command = ReplacePayslipFileCommandFromResourceAssembler.toCommandFromResource(payslipId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                payslipCommandService.handle(command), this::toResource, HttpStatus.OK);
    }

    /**
     * Publish a payslip.
     *
     * @param payslipId the payslip id
     * @return the HTTP response
     */
    @PatchMapping("/{payslipId}/publish")
    @Operation(summary = "Publish a payslip", description = "Makes the payslip visible to its employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payslip published",
                    content = @Content(schema = @Schema(implementation = PayslipResource.class))),
            @ApiResponse(responseCode = "404", description = "Payslip not found"),
            @ApiResponse(responseCode = "422", description = "Payslip already published")
    })
    public ResponseEntity<?> publishPayslip(@PathVariable Long payslipId) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                payslipCommandService.handle(new PublishPayslipCommand(payslipId)), this::toResource, HttpStatus.OK);
    }

    /**
     * Mark a payment as paid.
     *
     * @param payslipId the payslip id
     * @param resource  the {@link MarkPayslipAsPaidResource} instance
     * @return the HTTP response
     */
    @PatchMapping("/{payslipId}/mark-as-paid")
    @Operation(summary = "Mark a payment as paid", description = "Records the payment date of a published payslip.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment marked as paid",
                    content = @Content(schema = @Schema(implementation = PayslipResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Payslip not found"),
            @ApiResponse(responseCode = "422", description = "The payslip is not published")
    })
    public ResponseEntity<?> markPayslipAsPaid(@PathVariable Long payslipId,
                                               @Valid @RequestBody MarkPayslipAsPaidResource resource) {
        var command = MarkPayslipAsPaidCommandFromResourceAssembler.toCommandFromResource(payslipId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                payslipCommandService.handle(command), this::toResource, HttpStatus.OK);
    }

    /**
     * Mark a payment as observed.
     *
     * @param payslipId the payslip id
     * @param resource  the {@link MarkPayslipAsObservedResource} instance
     * @return the HTTP response
     */
    @PatchMapping("/{payslipId}/mark-as-observed")
    @Operation(summary = "Mark a payment as observed", description = "Records why the payment of a published payslip failed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment marked as observed",
                    content = @Content(schema = @Schema(implementation = PayslipResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Payslip not found"),
            @ApiResponse(responseCode = "422", description = "The payslip is not published")
    })
    public ResponseEntity<?> markPayslipAsObserved(@PathVariable Long payslipId,
                                                   @Valid @RequestBody MarkPayslipAsObservedResource resource) {
        var command = MarkPayslipAsObservedCommandFromResourceAssembler.toCommandFromResource(payslipId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                payslipCommandService.handle(command), this::toResource, HttpStatus.OK);
    }

    private PayslipResource toResource(Payslip payslip) {
        return PayslipResourceFromEntityAssembler.toResourceFromEntity(
                payslip, externalWorkspaceService.fetchEmployeeFullName(payslip.getEmployeeId().value()));
    }
}
