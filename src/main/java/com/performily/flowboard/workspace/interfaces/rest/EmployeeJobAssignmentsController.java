package com.performily.flowboard.workspace.interfaces.rest;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.performily.flowboard.workspace.application.commandservices.EmployeeCommandService;
import com.performily.flowboard.workspace.application.queryservices.EmployeeQueryService;
import com.performily.flowboard.workspace.domain.model.queries.GetEmployeeByIdQuery;
import com.performily.flowboard.workspace.interfaces.rest.resources.AssignJobResource;
import com.performily.flowboard.workspace.interfaces.rest.resources.JobAssignmentResource;
import com.performily.flowboard.workspace.interfaces.rest.transform.AssignJobCommandFromResourceAssembler;
import com.performily.flowboard.workspace.interfaces.rest.transform.JobAssignmentResourceFromEntityAssembler;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Employee Job Assignments Controller
 * @summary
 * REST controller for the job history (area and position changes) of an employee.
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/employees/{employeeId}/job-assignments", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Employees", description = "Employee job history endpoints")
public class EmployeeJobAssignmentsController {
    private final EmployeeCommandService employeeCommandService;
    private final EmployeeQueryService employeeQueryService;

    /**
     * Constructor.
     *
     * @param employeeCommandService the {@link EmployeeCommandService} instance
     * @param employeeQueryService the {@link EmployeeQueryService} instance
     */
    public EmployeeJobAssignmentsController(EmployeeCommandService employeeCommandService,
                                            EmployeeQueryService employeeQueryService) {
        this.employeeCommandService = employeeCommandService;
        this.employeeQueryService = employeeQueryService;
    }

    /**
     * Get job history.
     *
     * @param employeeId the employee id
     * @return the HTTP response
     */
    @GetMapping
    @Operation(summary = "Get job history", description = "Retrieves every area and position held by the employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job history retrieved successfully",
                    content = @Content(schema = @Schema(implementation = JobAssignmentResource.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<?> getJobAssignments(
            @PathVariable @Parameter(description = "Employee unique identifier", example = "1", required = true) Long employeeId) {
        var employee = employeeQueryService.handle(new GetEmployeeByIdQuery(employeeId));
        if (employee.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(ApplicationError.notFound("Employee", employeeId.toString()));
        }
        var resources = employee.get().getJobAssignments().stream()
                .map(JobAssignmentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Assign area and position.
     *
     * @param employeeId the employee id
     * @param resource the {@link AssignJobResource} instance
     * @return the HTTP response
     */
    @PostMapping
    @Operation(summary = "Assign area and position",
            description = "Closes the current job assignment and opens a new one (REASSIGNMENT) on the effective date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Job assigned successfully",
                    content = @Content(schema = @Schema(implementation = JobAssignmentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or position does not belong to the area"),
            @ApiResponse(responseCode = "404", description = "Employee, area or position not found"),
            @ApiResponse(responseCode = "422", description = "Employee is terminated")
    })
    public ResponseEntity<?> assignJob(
            @PathVariable @Parameter(description = "Employee unique identifier", example = "1", required = true) Long employeeId,
            @Valid @RequestBody AssignJobResource resource) {
        var command = AssignJobCommandFromResourceAssembler.toCommandFromResource(employeeId, resource);
        var result = employeeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                employee -> JobAssignmentResourceFromEntityAssembler.toResourceFromEntity(employee.getCurrentJobAssignment()),
                HttpStatus.CREATED);
    }
}