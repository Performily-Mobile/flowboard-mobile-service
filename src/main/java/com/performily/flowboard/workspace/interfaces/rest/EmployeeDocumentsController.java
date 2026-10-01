package com.performily.flowboard.workspace.interfaces.rest;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.performily.flowboard.workspace.application.commandservices.EmployeeCommandService;
import com.performily.flowboard.workspace.application.queryservices.EmployeeQueryService;
import com.performily.flowboard.workspace.domain.model.queries.GetEmployeeByIdQuery;
import com.performily.flowboard.workspace.interfaces.rest.resources.AttachEmployeeDocumentResource;
import com.performily.flowboard.workspace.interfaces.rest.resources.EmployeeDocumentResource;
import com.performily.flowboard.workspace.interfaces.rest.transform.AttachEmployeeDocumentCommandFromResourceAssembler;
import com.performily.flowboard.workspace.interfaces.rest.transform.EmployeeDocumentResourceFromEntityAssembler;
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
 * Employee Documents Controller
 * @summary
 * REST controller for the documents attached to an employee record.
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/employees/{employeeId}/documents", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Employees", description = "Employee document endpoints")
public class EmployeeDocumentsController {
    private final EmployeeCommandService employeeCommandService;
    private final EmployeeQueryService employeeQueryService;

    /**
     * Constructor.
     *
     * @param employeeCommandService the {@link EmployeeCommandService} instance
     * @param employeeQueryService the {@link EmployeeQueryService} instance
     */
    public EmployeeDocumentsController(EmployeeCommandService employeeCommandService,
                                       EmployeeQueryService employeeQueryService) {
        this.employeeCommandService = employeeCommandService;
        this.employeeQueryService = employeeQueryService;
    }

    /**
     * Get employee documents.
     *
     * @param employeeId the employee id
     * @return the HTTP response
     */
    @GetMapping
    @Operation(summary = "Get employee documents", description = "Retrieves the documents attached to the employee record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents retrieved successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeDocumentResource.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<?> getDocuments(
            @PathVariable @Parameter(description = "Employee unique identifier", example = "1", required = true) Long employeeId) {
        var employee = employeeQueryService.handle(new GetEmployeeByIdQuery(employeeId));
        if (employee.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(ApplicationError.notFound("Employee", employeeId.toString()));
        }
        var resources = employee.get().getDocuments().stream()
                .map(EmployeeDocumentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Attach document.
     *
     * @param employeeId the employee id
     * @param resource the {@link AttachEmployeeDocumentResource} instance
     * @return the HTTP response
     */
    @PostMapping
    @Operation(summary = "Attach document",
            description = "Registers a document (PDF, JPG or PNG, up to 5 MB) already uploaded to the storage service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Document attached successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeDocumentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid document type, file type or size"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<?> attachDocument(
            @PathVariable @Parameter(description = "Employee unique identifier", example = "1", required = true) Long employeeId,
            @Valid @RequestBody AttachEmployeeDocumentResource resource) {
        var command = AttachEmployeeDocumentCommandFromResourceAssembler.toCommandFromResource(employeeId, resource);
        var result = employeeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, EmployeeDocumentResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }
}