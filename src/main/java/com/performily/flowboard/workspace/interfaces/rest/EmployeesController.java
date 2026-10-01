package com.performily.flowboard.workspace.interfaces.rest;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.performily.flowboard.workspace.application.commandservices.EmployeeCommandService;
import com.performily.flowboard.workspace.application.queryservices.EmployeeQueryService;
import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import com.performily.flowboard.workspace.domain.model.commands.RemoveDirectManagerCommand;
import com.performily.flowboard.workspace.domain.model.commands.SuspendEmployeeCommand;
import com.performily.flowboard.workspace.domain.model.queries.GetAllEmployeesByDirectManagerIdQuery;
import com.performily.flowboard.workspace.domain.model.queries.GetEmployeeByIdQuery;
import com.performily.flowboard.workspace.domain.model.queries.GetOrganizationChartQuery;
import com.performily.flowboard.workspace.domain.model.queries.SearchEmployeesQuery;
import com.performily.flowboard.workspace.domain.model.valueobjects.EmploymentStatus;
import com.performily.flowboard.workspace.interfaces.rest.resources.*;
import com.performily.flowboard.workspace.interfaces.rest.transform.*;
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
import java.util.Locale;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Employees Controller
 * @summary
 * REST controller that exposes the employees, their status, hierarchy and organization chart.
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/employees", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Employees", description = "Employee management endpoints")
public class EmployeesController {
    private final EmployeeCommandService employeeCommandService;
    private final EmployeeQueryService employeeQueryService;

    /**
     * Constructor.
     *
     * @param employeeCommandService the {@link EmployeeCommandService} instance
     * @param employeeQueryService the {@link EmployeeQueryService} instance
     */
    public EmployeesController(EmployeeCommandService employeeCommandService, EmployeeQueryService employeeQueryService) {
        this.employeeCommandService = employeeCommandService;
        this.employeeQueryService = employeeQueryService;
    }

    /**
     * Register a new employee.
     *
     * @param resource the {@link RegisterEmployeeResource} instance
     * @return the HTTP response
     */
    @PostMapping
    @Operation(summary = "Register a new employee",
            description = "Registers a new ACTIVE employee and opens its first job assignment (HIRE).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee registered successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Area or position not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - identity document or e-mail already exists")
    })
    public ResponseEntity<?> registerEmployee(@Valid @RequestBody RegisterEmployeeResource resource) {
        var registerEmployeeCommand = RegisterEmployeeCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = employeeCommandService.handle(registerEmployeeCommand)
                .flatMap(employeeId -> employeeQueryService.handle(new GetEmployeeByIdQuery(employeeId))
                        .<Result<Employee, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Employee", employeeId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, EmployeeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    /**
     * Get employees.
     *
     * @param search the text to search in the names or the identity document number
     * @param areaId the area id
     * @param status the employment status
     * @param positionId the position id
     * @return the HTTP response
     */
    @GetMapping
    @Operation(summary = "Get employees",
            description = "Retrieves the employees ordered by last name. Every filter is optional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employees retrieved successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid filter")
    })
    public ResponseEntity<List<EmployeeResource>> getAllEmployees(
            @RequestParam(required = false) @Parameter(description = "Name, last name or identity document number", example = "Espinoza") String search,
            @RequestParam(required = false) @Parameter(description = "Area identifier", example = "1") Long areaId,
            @RequestParam(required = false) @Parameter(description = "Employment status", example = "ACTIVE") String status,
            @RequestParam(required = false) @Parameter(description = "Position identifier", example = "1") Long positionId) {
        var employmentStatus = status == null || status.isBlank()
                ? null
                : EmploymentStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        var employees = employeeQueryService.handle(new SearchEmployeesQuery(search, areaId, employmentStatus, positionId));
        var employeeResources = employees.stream().map(EmployeeResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(employeeResources);
    }

    /**
     * Get organization chart.
     *
     * @param areaId the area id
     * @return the HTTP response
     */
    @GetMapping("/organization-chart")
    @Operation(summary = "Get organization chart",
            description = "Builds the organization chart from the direct manager of each employee that is not TERMINATED. "
                    + "Employees whose direct manager is no longer active are listed as pending reassignment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organization chart retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrganizationChartResource.class)))
    })
    public ResponseEntity<OrganizationChartResource> getOrganizationChart(
            @RequestParam(required = false) @Parameter(description = "Show only this area", example = "1") Long areaId) {
        var employees = employeeQueryService.handle(new GetOrganizationChartQuery());
        return ResponseEntity.ok(OrganizationChartNodeResourceFromEntityAssembler.toResourceFromEntities(employees, areaId));
    }

    /**
     * Get employee by ID.
     *
     * @param employeeId the employee id
     * @return the HTTP response
     */
    @GetMapping("/{employeeId}")
    @Operation(summary = "Get employee by ID", description = "Retrieves a specific employee by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee found",
                    content = @Content(schema = @Schema(implementation = EmployeeResource.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<?> getEmployeeById(
            @PathVariable @Parameter(description = "Employee unique identifier", example = "1", required = true) Long employeeId) {
        var employee = employeeQueryService.handle(new GetEmployeeByIdQuery(employeeId));
        if (employee.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(ApplicationError.notFound("Employee", employeeId.toString()));
        }
        return ResponseEntity.ok(EmployeeResourceFromEntityAssembler.toResourceFromEntity(employee.get()));
    }

    /**
     * Update personal data.
     *
     * @param employeeId the employee id
     * @param resource the {@link UpdateEmployeePersonalDataResource} instance
     * @return the HTTP response
     */
    @PutMapping("/{employeeId}")
    @Operation(summary = "Update personal data", description = "Updates the name, birth date, contact information and address of an employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personal data updated successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - e-mail already exists")
    })
    public ResponseEntity<?> updatePersonalData(
            @PathVariable @Parameter(description = "Employee unique identifier", example = "1", required = true) Long employeeId,
            @Valid @RequestBody UpdateEmployeePersonalDataResource resource) {
        var command = UpdateEmployeePersonalDataCommandFromResourceAssembler.toCommandFromResource(employeeId, resource);
        var result = employeeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, EmployeeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Assign direct manager.
     *
     * @param employeeId the employee id
     * @param resource the {@link AssignDirectManagerResource} instance
     * @return the HTTP response
     */
    @PutMapping("/{employeeId}/direct-manager")
    @Operation(summary = "Assign direct manager",
            description = "Assigns the direct manager. An employee cannot be its own manager and the hierarchy cannot contain cycles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Direct manager assigned successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Employee or manager not found"),
            @ApiResponse(responseCode = "422", description = "Inactive manager or cycle in the hierarchy")
    })
    public ResponseEntity<?> assignDirectManager(
            @PathVariable @Parameter(description = "Employee unique identifier", example = "1", required = true) Long employeeId,
            @Valid @RequestBody AssignDirectManagerResource resource) {
        var command = AssignDirectManagerCommandFromResourceAssembler.toCommandFromResource(employeeId, resource);
        var result = employeeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, EmployeeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Remove direct manager.
     *
     * @param employeeId the employee id
     * @return the HTTP response
     */
    @DeleteMapping("/{employeeId}/direct-manager")
    @Operation(summary = "Remove direct manager", description = "Removes the direct manager of an employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Direct manager removed successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResource.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "422", description = "Employee has no direct manager")
    })
    public ResponseEntity<?> removeDirectManager(
            @PathVariable @Parameter(description = "Employee unique identifier", example = "1", required = true) Long employeeId) {
        var result = employeeCommandService.handle(new RemoveDirectManagerCommand(employeeId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, EmployeeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Get direct subordinates.
     *
     * @param employeeId the employee id
     * @return the HTTP response
     */
    @GetMapping("/{employeeId}/subordinates")
    @Operation(summary = "Get direct subordinates", description = "Retrieves the employees whose direct manager is the given employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subordinates retrieved successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResource.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<?> getSubordinates(
            @PathVariable @Parameter(description = "Employee unique identifier", example = "1", required = true) Long employeeId) {
        if (employeeQueryService.handle(new GetEmployeeByIdQuery(employeeId)).isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(ApplicationError.notFound("Employee", employeeId.toString()));
        }
        var subordinates = employeeQueryService.handle(new GetAllEmployeesByDirectManagerIdQuery(employeeId));
        return ResponseEntity.ok(subordinates.stream().map(EmployeeResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Suspend employee.
     *
     * @param employeeId the employee id
     * @return the HTTP response
     */
    @PatchMapping("/{employeeId}/suspend")
    @Operation(summary = "Suspend employee", description = "Suspends an ACTIVE employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee suspended successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResource.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "422", description = "Employee is not active")
    })
    public ResponseEntity<?> suspendEmployee(
            @PathVariable @Parameter(description = "Employee unique identifier", example = "1", required = true) Long employeeId) {
        var result = employeeCommandService.handle(new SuspendEmployeeCommand(employeeId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, EmployeeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Terminate employee.
     *
     * @param employeeId the employee id
     * @param resource the {@link TerminateEmployeeResource} instance
     * @return the HTTP response
     */
    @PatchMapping("/{employeeId}/terminate")
    @Operation(summary = "Terminate employee",
            description = "Terminates an employee. An employee with subordinates cannot be terminated until they are reassigned.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee terminated successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "422", description = "Employee has subordinates or is already terminated")
    })
    public ResponseEntity<?> terminateEmployee(
            @PathVariable @Parameter(description = "Employee unique identifier", example = "1", required = true) Long employeeId,
            @Valid @RequestBody TerminateEmployeeResource resource) {
        var command = TerminateEmployeeCommandFromResourceAssembler.toCommandFromResource(employeeId, resource);
        var result = employeeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, EmployeeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Reinstate employee.
     *
     * @param employeeId the employee id
     * @param resource the {@link ReinstateEmployeeResource} instance
     * @return the HTTP response
     */
    @PatchMapping("/{employeeId}/reinstate")
    @Operation(summary = "Reinstate employee",
            description = "Reinstates a TERMINATED employee, opening a new employment period and job assignment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee reinstated successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Employee, area or position not found"),
            @ApiResponse(responseCode = "409", description = "Identity document already belongs to an active employee"),
            @ApiResponse(responseCode = "422", description = "Employee is not terminated")
    })
    public ResponseEntity<?> reinstateEmployee(
            @PathVariable @Parameter(description = "Employee unique identifier", example = "1", required = true) Long employeeId,
            @Valid @RequestBody ReinstateEmployeeResource resource) {
        var command = ReinstateEmployeeCommandFromResourceAssembler.toCommandFromResource(employeeId, resource);
        var result = employeeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, EmployeeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}