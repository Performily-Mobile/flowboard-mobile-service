package com.performily.flowboard.request.interfaces.rest;

import com.performily.flowboard.request.application.commandservices.RequestTypeCommandService;
import com.performily.flowboard.request.application.queryservices.RequestTypeQueryService;
import com.performily.flowboard.request.domain.model.commands.ActivateRequestTypeCommand;
import com.performily.flowboard.request.domain.model.commands.DeactivateRequestTypeCommand;
import com.performily.flowboard.request.domain.model.commands.DeleteRequestTypeCommand;
import com.performily.flowboard.request.domain.model.commands.RemoveRequestFieldCommand;
import com.performily.flowboard.request.domain.model.entities.RequestType;
import com.performily.flowboard.request.domain.model.queries.GetAllRequestTypesQuery;
import com.performily.flowboard.request.domain.model.queries.GetRequestTypeByIdQuery;
import com.performily.flowboard.request.interfaces.rest.resources.CreateRequestFieldResource;
import com.performily.flowboard.request.interfaces.rest.resources.CreateRequestTypeResource;
import com.performily.flowboard.request.interfaces.rest.resources.RequestTypeResource;
import com.performily.flowboard.request.interfaces.rest.resources.UpdateRequestTypeResource;
import com.performily.flowboard.request.interfaces.rest.transform.AddRequestFieldCommandFromResourceAssembler;
import com.performily.flowboard.request.interfaces.rest.transform.CreateRequestTypeCommandFromResourceAssembler;
import com.performily.flowboard.request.interfaces.rest.transform.RequestTypeResourceFromEntityAssembler;
import com.performily.flowboard.request.interfaces.rest.transform.UpdateRequestTypeCommandFromResourceAssembler;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.interfaces.rest.resources.MessageResource;
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
 * Request Types Controller
 * @summary
 * REST controller that exposes the catalog of request types and the fields of their forms.
 * The administration endpoints are for the HR staff; the role check will be added with IAM.
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/request-types", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Request Types", description = "Request type catalog management endpoints")
public class RequestTypesController {
    private final RequestTypeCommandService requestTypeCommandService;
    private final RequestTypeQueryService requestTypeQueryService;

    /**
     * Constructor.
     *
     * @param requestTypeCommandService the {@link RequestTypeCommandService} instance
     * @param requestTypeQueryService the {@link RequestTypeQueryService} instance
     */
    public RequestTypesController(RequestTypeCommandService requestTypeCommandService,
                                  RequestTypeQueryService requestTypeQueryService) {
        this.requestTypeCommandService = requestTypeCommandService;
        this.requestTypeQueryService = requestTypeQueryService;
    }

    /**
     * Create a new request type.
     *
     * @param resource the {@link CreateRequestTypeResource} instance
     * @return the HTTP response
     */
    @PostMapping
    @Operation(summary = "Create a new request type",
            description = "Creates a new active request type, optionally with the fields of its form. The name must be unique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request type created successfully",
                    content = @Content(schema = @Schema(implementation = RequestTypeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Conflict - request type name already exists")
    })
    public ResponseEntity<?> createRequestType(@Valid @RequestBody CreateRequestTypeResource resource) {
        var command = CreateRequestTypeCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = requestTypeCommandService.handle(command)
                .flatMap(requestTypeId -> requestTypeQueryService.handle(new GetRequestTypeByIdQuery(requestTypeId))
                        .<Result<RequestType, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("RequestType", requestTypeId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RequestTypeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    /**
     * Get all request types.
     *
     * @param activeOnly whether only the active types are returned
     * @return the HTTP response
     */
    @GetMapping
    @Operation(summary = "Get all request types",
            description = "Retrieves the request types with their fields. With activeOnly=true only the active ones are returned; the app uses them to build the form.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request types retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RequestTypeResource.class)))
    })
    public ResponseEntity<List<RequestTypeResource>> getAllRequestTypes(
            @RequestParam(defaultValue = "false") @Parameter(description = "Return only the active types", example = "true")
            boolean activeOnly) {
        var requestTypes = requestTypeQueryService.handle(new GetAllRequestTypesQuery(activeOnly));
        return ResponseEntity.ok(requestTypes.stream().map(RequestTypeResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Get request type by ID.
     *
     * @param requestTypeId the request type id
     * @return the HTTP response
     */
    @GetMapping("/{requestTypeId}")
    @Operation(summary = "Get request type by ID", description = "Retrieves a request type with the fields of its form.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request type found",
                    content = @Content(schema = @Schema(implementation = RequestTypeResource.class))),
            @ApiResponse(responseCode = "404", description = "Request type not found")
    })
    public ResponseEntity<?> getRequestTypeById(
            @PathVariable @Parameter(description = "Request type unique identifier", example = "1", required = true) Long requestTypeId) {
        var requestType = requestTypeQueryService.handle(new GetRequestTypeByIdQuery(requestTypeId));
        if (requestType.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    ApplicationError.notFound("RequestType", requestTypeId.toString()));
        }
        return ResponseEntity.ok(RequestTypeResourceFromEntityAssembler.toResourceFromEntity(requestType.get()));
    }

    /**
     * Update request type.
     *
     * @param requestTypeId the request type id
     * @param resource the {@link UpdateRequestTypeResource} instance
     * @return the HTTP response
     */
    @PutMapping("/{requestTypeId}")
    @Operation(summary = "Update request type", description = "Updates the name, description, attachment flag and balance deduction of a request type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request type updated successfully",
                    content = @Content(schema = @Schema(implementation = RequestTypeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Request type not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - request type name already exists")
    })
    public ResponseEntity<?> updateRequestType(
            @PathVariable @Parameter(description = "Request type unique identifier", example = "1", required = true) Long requestTypeId,
            @Valid @RequestBody UpdateRequestTypeResource resource) {
        var command = UpdateRequestTypeCommandFromResourceAssembler.toCommandFromResource(requestTypeId, resource);
        var result = requestTypeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RequestTypeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Delete request type.
     *
     * @param requestTypeId the request type id
     * @return the HTTP response
     */
    @DeleteMapping("/{requestTypeId}")
    @Operation(summary = "Delete request type",
            description = "Deletes a request type that has no requests. A type with requests must be deactivated instead.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request type deleted successfully",
                    content = @Content(schema = @Schema(implementation = MessageResource.class))),
            @ApiResponse(responseCode = "404", description = "Request type not found"),
            @ApiResponse(responseCode = "422", description = "Request type has requests")
    })
    public ResponseEntity<?> deleteRequestType(
            @PathVariable @Parameter(description = "Request type unique identifier", example = "1", required = true) Long requestTypeId) {
        var result = requestTypeCommandService.handle(new DeleteRequestTypeCommand(requestTypeId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, id -> new MessageResource("Request type %d deleted".formatted(id)), HttpStatus.OK);
    }

    /**
     * Add a field to a request type.
     *
     * @param requestTypeId the request type id
     * @param resource the {@link CreateRequestFieldResource} instance
     * @return the HTTP response
     */
    @PostMapping("/{requestTypeId}/fields")
    @Operation(summary = "Add field to request type", description = "Adds a field to the form of a request type. The key must be unique inside the type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Field added successfully",
                    content = @Content(schema = @Schema(implementation = RequestTypeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or repeated key"),
            @ApiResponse(responseCode = "404", description = "Request type not found")
    })
    public ResponseEntity<?> addField(
            @PathVariable @Parameter(description = "Request type unique identifier", example = "1", required = true) Long requestTypeId,
            @Valid @RequestBody CreateRequestFieldResource resource) {
        var command = AddRequestFieldCommandFromResourceAssembler.toCommandFromResource(requestTypeId, resource);
        var result = requestTypeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RequestTypeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    /**
     * Remove a field from a request type.
     *
     * @param requestTypeId the request type id
     * @param fieldId the field id
     * @return the HTTP response
     */
    @DeleteMapping("/{requestTypeId}/fields/{fieldId}")
    @Operation(summary = "Remove field from request type", description = "Removes a field from the form. Only allowed while the type has no requests.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Field removed successfully",
                    content = @Content(schema = @Schema(implementation = RequestTypeResource.class))),
            @ApiResponse(responseCode = "400", description = "Field does not belong to the request type"),
            @ApiResponse(responseCode = "404", description = "Request type not found"),
            @ApiResponse(responseCode = "422", description = "Request type has requests")
    })
    public ResponseEntity<?> removeField(
            @PathVariable @Parameter(description = "Request type unique identifier", example = "1", required = true) Long requestTypeId,
            @PathVariable @Parameter(description = "Field unique identifier", example = "1", required = true) Long fieldId) {
        var result = requestTypeCommandService.handle(new RemoveRequestFieldCommand(requestTypeId, fieldId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RequestTypeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Activate request type.
     *
     * @param requestTypeId the request type id
     * @return the HTTP response
     */
    @PatchMapping("/{requestTypeId}/activate")
    @Operation(summary = "Activate request type", description = "Activates an inactive request type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request type activated successfully",
                    content = @Content(schema = @Schema(implementation = RequestTypeResource.class))),
            @ApiResponse(responseCode = "404", description = "Request type not found"),
            @ApiResponse(responseCode = "422", description = "Request type is already active")
    })
    public ResponseEntity<?> activateRequestType(
            @PathVariable @Parameter(description = "Request type unique identifier", example = "1", required = true) Long requestTypeId) {
        var result = requestTypeCommandService.handle(new ActivateRequestTypeCommand(requestTypeId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RequestTypeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Deactivate request type.
     *
     * @param requestTypeId the request type id
     * @return the HTTP response
     */
    @PatchMapping("/{requestTypeId}/deactivate")
    @Operation(summary = "Deactivate request type", description = "Deactivates a request type. It stops receiving new requests but keeps its history.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request type deactivated successfully",
                    content = @Content(schema = @Schema(implementation = RequestTypeResource.class))),
            @ApiResponse(responseCode = "404", description = "Request type not found"),
            @ApiResponse(responseCode = "422", description = "Request type is already inactive")
    })
    public ResponseEntity<?> deactivateRequestType(
            @PathVariable @Parameter(description = "Request type unique identifier", example = "1", required = true) Long requestTypeId) {
        var result = requestTypeCommandService.handle(new DeactivateRequestTypeCommand(requestTypeId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RequestTypeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
