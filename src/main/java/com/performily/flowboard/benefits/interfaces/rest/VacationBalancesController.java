package com.performily.flowboard.benefits.interfaces.rest;

import com.performily.flowboard.benefits.application.commandservices.VacationBalanceCommandService;
import com.performily.flowboard.benefits.application.queryservices.BenefitQueryService;
import com.performily.flowboard.benefits.domain.model.queries.GetAllVacationBalancesQuery;
import com.performily.flowboard.benefits.domain.model.queries.GetVacationBalanceByEmployeeIdQuery;
import com.performily.flowboard.benefits.domain.model.queries.GetVacationMovementsByEmployeeIdQuery;
import com.performily.flowboard.benefits.interfaces.rest.resources.AdjustVacationBalanceResource;
import com.performily.flowboard.benefits.interfaces.rest.resources.VacationBalanceResource;
import com.performily.flowboard.benefits.interfaces.rest.transform.VacationBalanceResourceFromEntityAssembler;
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Vacation Balances Controller
 * @summary
 * REST controller of the vacation balances (US41) and their manual adjustment (US42).
 *
 * Usage and reversal have no endpoint on purpose: they happen only when Request
 * approves or cancels a vacation request (RequestApprovedEvent / RequestCancelledEvent).
 * Until IAM is implemented, /me receives the employeeId as a parameter.
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/vacation-balances", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Vacation Balances", description = "Vacation balances, movements and manual adjustments")
public class VacationBalancesController {
    private final VacationBalanceCommandService vacationBalanceCommandService;
    private final BenefitQueryService benefitQueryService;

    public VacationBalancesController(VacationBalanceCommandService vacationBalanceCommandService,
                                      BenefitQueryService benefitQueryService) {
        this.vacationBalanceCommandService = vacationBalanceCommandService;
        this.benefitQueryService = benefitQueryService;
    }

    @GetMapping
    @Operation(summary = "List the vacation balances of the active employees",
            description = "Optionally of one area, from the highest to the lowest available days.")
    public ResponseEntity<List<VacationBalanceResource>> getAllVacationBalances(@RequestParam(required = false) Long areaId) {
        return ResponseEntity.ok(benefitQueryService.handle(new GetAllVacationBalancesQuery(areaId)).stream()
                .map(VacationBalanceResourceFromEntityAssembler::toResourceFromView).toList());
    }

    @GetMapping("/me")
    @Operation(summary = "Vacation balance of the employee (US41)",
            description = "Accrued, used and available days with the movements. employeeId will come from the token when IAM exists.")
    public ResponseEntity<?> getMyVacationBalance(@RequestParam Long employeeId) {
        return getVacationBalance(employeeId);
    }

    @GetMapping("/me/movements")
    @Operation(summary = "Movements of the vacation balance of the employee", description = "Optionally between two dates.")
    public ResponseEntity<?> getMyVacationMovements(
            @RequestParam Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return getVacationMovements(employeeId, fromDate, toDate);
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Vacation balance of an employee (US41)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance found"),
            @ApiResponse(responseCode = "404", description = "The employee has no vacation balance")
    })
    public ResponseEntity<?> getVacationBalance(@PathVariable Long employeeId) {
        var result = benefitQueryService.handle(new GetVacationBalanceByEmployeeIdQuery(employeeId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, VacationBalanceResourceFromEntityAssembler::toResourceFromView, HttpStatus.OK);
    }

    @GetMapping("/{employeeId}/movements")
    @Operation(summary = "Movements of the vacation balance of an employee", description = "Optionally between two dates.")
    public ResponseEntity<?> getVacationMovements(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        var result = benefitQueryService.handle(new GetVacationMovementsByEmployeeIdQuery(employeeId, fromDate, toDate));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, VacationBalanceResourceFromEntityAssembler::toResourcesFromViews, HttpStatus.OK);
    }

    @PostMapping("/{employeeId}/adjustments")
    @Operation(summary = "Adjust a vacation balance manually (US42)",
            description = "The reason is required and the movement keeps its author. It cannot leave the balance negative.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Adjustment registered"),
            @ApiResponse(responseCode = "400", description = "Missing reason or invalid days"),
            @ApiResponse(responseCode = "404", description = "Balance or author not found"),
            @ApiResponse(responseCode = "422", description = "The adjustment would leave the balance negative")
    })
    public ResponseEntity<?> adjustVacationBalance(@PathVariable Long employeeId,
                                                   @Valid @RequestBody AdjustVacationBalanceResource resource) {
        var command = VacationBalanceResourceFromEntityAssembler.toCommandFromResource(employeeId, resource);
        var result = vacationBalanceCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, VacationBalanceResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }
}
