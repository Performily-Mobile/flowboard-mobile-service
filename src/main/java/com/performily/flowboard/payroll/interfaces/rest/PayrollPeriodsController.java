package com.performily.flowboard.payroll.interfaces.rest;

import com.performily.flowboard.payroll.application.commandservices.PayrollPeriodCommandService;
import com.performily.flowboard.payroll.application.commandservices.PayslipCommandService;
import com.performily.flowboard.payroll.application.queryservices.PayrollPeriodQueryService;
import com.performily.flowboard.payroll.domain.model.commands.PublishPayrollPeriodPayslipsCommand;
import com.performily.flowboard.payroll.domain.model.entities.PayrollPeriod;
import com.performily.flowboard.payroll.domain.model.queries.GetAllPayrollPeriodsQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayrollPeriodByIdQuery;
import com.performily.flowboard.payroll.interfaces.rest.resources.CreatePayrollPeriodResource;
import com.performily.flowboard.payroll.interfaces.rest.resources.PayrollPeriodResource;
import com.performily.flowboard.payroll.interfaces.rest.resources.PublishedPayslipsResource;
import com.performily.flowboard.payroll.interfaces.rest.transform.CreatePayrollPeriodCommandFromResourceAssembler;
import com.performily.flowboard.payroll.interfaces.rest.transform.PayrollPeriodResourceFromEntityAssembler;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Payroll Periods Controller
 * @summary
 * REST controller that exposes the payroll periods (HR).
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/payroll-periods", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Payroll Periods", description = "Payroll period management endpoints")
public class PayrollPeriodsController {
    private final PayrollPeriodCommandService payrollPeriodCommandService;
    private final PayrollPeriodQueryService payrollPeriodQueryService;
    private final PayslipCommandService payslipCommandService;

    /**
     * Constructor.
     *
     * @param payrollPeriodCommandService the {@link PayrollPeriodCommandService} instance
     * @param payrollPeriodQueryService   the {@link PayrollPeriodQueryService} instance
     * @param payslipCommandService       the {@link PayslipCommandService} instance, used to publish a whole period
     */
    public PayrollPeriodsController(PayrollPeriodCommandService payrollPeriodCommandService,
                                    PayrollPeriodQueryService payrollPeriodQueryService,
                                    PayslipCommandService payslipCommandService) {
        this.payrollPeriodCommandService = payrollPeriodCommandService;
        this.payrollPeriodQueryService = payrollPeriodQueryService;
        this.payslipCommandService = payslipCommandService;
    }

    /**
     * Create a payroll period.
     *
     * @param resource the {@link CreatePayrollPeriodResource} instance
     * @return the HTTP response
     */
    @PostMapping
    @Operation(summary = "Create a payroll period",
            description = "Opens a payroll period for a month. There is only one period per month and it cannot be a future month.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payroll period created successfully",
                    content = @Content(schema = @Schema(implementation = PayrollPeriodResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Conflict - the period already exists")
    })
    public ResponseEntity<?> createPayrollPeriod(@Valid @RequestBody CreatePayrollPeriodResource resource) {
        var command = CreatePayrollPeriodCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = payrollPeriodCommandService.handle(command)
                .flatMap(periodId -> payrollPeriodQueryService.handle(new GetPayrollPeriodByIdQuery(periodId))
                        .<Result<PayrollPeriod, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("PayrollPeriod", periodId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, PayrollPeriodResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    /**
     * Get all payroll periods.
     *
     * @return the HTTP response
     */
    @GetMapping
    @Operation(summary = "Get all payroll periods", description = "Retrieves every payroll period, the most recent first.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payroll periods retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PayrollPeriodResource.class)))
    })
    public ResponseEntity<List<PayrollPeriodResource>> getAllPayrollPeriods() {
        var periods = payrollPeriodQueryService.handle(new GetAllPayrollPeriodsQuery());
        return ResponseEntity.ok(periods.stream().map(PayrollPeriodResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Get payroll period by ID.
     *
     * @param payrollPeriodId the payroll period id
     * @return the HTTP response
     */
    @GetMapping("/{payrollPeriodId}")
    @Operation(summary = "Get payroll period by ID", description = "Retrieves a payroll period by its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payroll period found",
                    content = @Content(schema = @Schema(implementation = PayrollPeriodResource.class))),
            @ApiResponse(responseCode = "404", description = "Payroll period not found")
    })
    public ResponseEntity<?> getPayrollPeriodById(
            @PathVariable @Parameter(description = "Payroll period unique identifier", example = "3", required = true) Long payrollPeriodId) {
        var period = payrollPeriodQueryService.handle(new GetPayrollPeriodByIdQuery(payrollPeriodId));
        if (period.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    ApplicationError.notFound("PayrollPeriod", payrollPeriodId.toString()));
        }
        return ResponseEntity.ok(PayrollPeriodResourceFromEntityAssembler.toResourceFromEntity(period.get()));
    }

    /**
     * Publish every payslip of a payroll period.
     *
     * @param payrollPeriodId the payroll period id
     * @return the HTTP response
     */
    @PatchMapping("/{payrollPeriodId}/publish-payslips")
    @Operation(summary = "Publish the payslips of a period",
            description = "Publishes every payslip of the period that is still UNDER_REVIEW.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payslips published",
                    content = @Content(schema = @Schema(implementation = PublishedPayslipsResource.class))),
            @ApiResponse(responseCode = "404", description = "Payroll period not found")
    })
    public ResponseEntity<?> publishPayrollPeriodPayslips(@PathVariable Long payrollPeriodId) {
        var result = payslipCommandService.handle(new PublishPayrollPeriodPayslipsCommand(payrollPeriodId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, count -> new PublishedPayslipsResource(payrollPeriodId, count), HttpStatus.OK);
    }
}
