package com.performily.flowboard.request.interfaces.rest;

import com.performily.flowboard.request.application.commandservices.RequestCommandService;
import com.performily.flowboard.request.application.queryservices.RequestQueryService;
import com.performily.flowboard.request.domain.model.aggregates.Request;
import com.performily.flowboard.request.domain.model.commands.ApproveRequestCommand;
import com.performily.flowboard.request.domain.model.commands.CancelRequestCommand;
import com.performily.flowboard.request.domain.model.commands.RejectRequestCommand;
import com.performily.flowboard.request.domain.model.commands.ReturnRequestForReviewCommand;
import com.performily.flowboard.request.domain.model.queries.GetPendingRequestsByApproverIdQuery;
import com.performily.flowboard.request.domain.model.queries.GetPendingRequestsForHrStaffQuery;
import com.performily.flowboard.request.domain.model.queries.GetRequestByIdQuery;
import com.performily.flowboard.request.domain.model.queries.GetRequestsByRequesterIdQuery;
import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;
import com.performily.flowboard.request.interfaces.rest.resources.RequestResource;
import com.performily.flowboard.request.interfaces.rest.resources.ResolveRequestResource;
import com.performily.flowboard.request.interfaces.rest.resources.ResubmitRequestResource;
import com.performily.flowboard.request.interfaces.rest.resources.SubmitRequestResource;
import com.performily.flowboard.request.interfaces.rest.transform.RequestResourceFromEntityAssembler;
import com.performily.flowboard.request.interfaces.rest.transform.RequestEnumAssembler;
import com.performily.flowboard.request.interfaces.rest.transform.ResubmitRequestCommandFromResourceAssembler;
import com.performily.flowboard.request.interfaces.rest.transform.SubmitRequestCommandFromResourceAssembler;
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
 * Requests Controller
 * @summary
 * REST controller that exposes the submission, tracking and resolution of requests.
 *
 * Until IAM is implemented, the employee that performs each action (requesterId,
 * approverId, actorId) is sent by the client. When IAM exists it will be taken
 * from the token.
 *
 * Requests are never deleted, to keep the history: a request that is no longer
 * needed is cancelled.
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/requests", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Requests", description = "Request submission, tracking and approval endpoints")
public class RequestsController {
    private final RequestCommandService requestCommandService;
    private final RequestQueryService requestQueryService;

    /**
     * Constructor.
     *
     * @param requestCommandService the {@link RequestCommandService} instance
     * @param requestQueryService the {@link RequestQueryService} instance
     */
    public RequestsController(RequestCommandService requestCommandService, RequestQueryService requestQueryService) {
        this.requestCommandService = requestCommandService;
        this.requestQueryService = requestQueryService;
    }

    /**
     * Submit a new request.
     *
     * @param resource the {@link SubmitRequestResource} instance
     * @return the HTTP response
     */
    @PostMapping
    @Operation(summary = "Submit a new request",
            description = "Submits a request of the given type. It is validated against the fields of the type and routed to the direct manager of the requester, or to the HR staff when there is none.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request submitted successfully",
                    content = @Content(schema = @Schema(implementation = RequestResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data, missing required fields or missing attachment"),
            @ApiResponse(responseCode = "404", description = "Employee or request type not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - there is another request for the same days"),
            @ApiResponse(responseCode = "422", description = "Requester is not ACTIVE")
    })
    public ResponseEntity<?> submitRequest(@Valid @RequestBody SubmitRequestResource resource) {
        var command = SubmitRequestCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = requestCommandService.handle(command)
                .flatMap(requestId -> requestQueryService.handle(new GetRequestByIdQuery(requestId))
                        .<Result<Request, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Request", requestId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RequestResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    /**
     * Get the requests of an employee.
     *
     * @param requesterId the requester id
     * @param status the optional status filter
     * @return the HTTP response
     */
    @GetMapping("/me")
    @Operation(summary = "Get my requests",
            description = "Retrieves the requests of an employee, newest first. The status filter is optional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requests retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RequestResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status")
    })
    public ResponseEntity<List<RequestResource>> getMyRequests(
            @RequestParam @Parameter(description = "Requester employee id", example = "3", required = true) Long requesterId,
            @RequestParam(required = false) @Parameter(description = "Status filter",
                    schema = @Schema(allowableValues = {"IN_PROGRESS", "UNDER_REVIEW", "APPROVED", "REJECTED", "CANCELLED"}))
            String status) {
        var statusFilter = status == null || status.isBlank()
                ? null
                : RequestEnumAssembler.toEnum(RequestStatus.class, status, null, "status");
        var requests = requestQueryService.handle(new GetRequestsByRequesterIdQuery(requesterId, statusFilter));
        return ResponseEntity.ok(toResources(requests));
    }

    /**
     * Get the pending requests of a direct manager.
     *
     * @param approverId the approver id
     * @param requestTypeId the optional request type filter
     * @return the HTTP response
     */
    @GetMapping("/pending-approval")
    @Operation(summary = "Get pending requests of an approver",
            description = "Retrieves the IN_PROGRESS requests assigned to a direct manager, oldest first. The request type filter is optional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requests retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RequestResource.class)))
    })
    public ResponseEntity<List<RequestResource>> getPendingApproval(
            @RequestParam @Parameter(description = "Approver employee id", example = "2", required = true) Long approverId,
            @RequestParam(required = false) @Parameter(description = "Request type filter", example = "1") Long requestTypeId) {
        var requests = requestQueryService.handle(new GetPendingRequestsByApproverIdQuery(approverId, requestTypeId));
        return ResponseEntity.ok(toResources(requests));
    }

    /**
     * Get the pending requests routed to the HR staff.
     *
     * @param requestTypeId the optional request type filter
     * @return the HTTP response
     */
    @GetMapping("/pending-approval/hr-staff")
    @Operation(summary = "Get pending requests of the HR staff",
            description = "Retrieves the IN_PROGRESS requests of employees without direct manager, oldest first.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requests retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RequestResource.class)))
    })
    public ResponseEntity<List<RequestResource>> getPendingApprovalForHrStaff(
            @RequestParam(required = false) @Parameter(description = "Request type filter", example = "1") Long requestTypeId) {
        var requests = requestQueryService.handle(new GetPendingRequestsForHrStaffQuery(requestTypeId));
        return ResponseEntity.ok(toResources(requests));
    }

    /**
     * Get request by ID.
     *
     * @param requestId the request id
     * @return the HTTP response
     */
    @GetMapping("/{requestId}")
    @Operation(summary = "Get request by ID", description = "Retrieves a request with its form values, attachments and history.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request found",
                    content = @Content(schema = @Schema(implementation = RequestResource.class))),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    public ResponseEntity<?> getRequestById(
            @PathVariable @Parameter(description = "Request unique identifier", example = "1", required = true) Long requestId) {
        var request = requestQueryService.handle(new GetRequestByIdQuery(requestId));
        if (request.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    ApplicationError.notFound("Request", requestId.toString()));
        }
        return ResponseEntity.ok(RequestResourceFromEntityAssembler.toResourceFromEntity(request.get()));
    }

    /**
     * Approve a request.
     *
     * @param requestId the request id
     * @param resource the {@link ResolveRequestResource} instance
     * @return the HTTP response
     */
    @PostMapping("/{requestId}/approve")
    @Operation(summary = "Approve request", description = "Approves an IN_PROGRESS request. Only the assigned approver can do it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request approved successfully",
                    content = @Content(schema = @Schema(implementation = RequestResource.class))),
            @ApiResponse(responseCode = "404", description = "Request not found"),
            @ApiResponse(responseCode = "422", description = "Request is not IN_PROGRESS or the actor is not the assigned approver")
    })
    public ResponseEntity<?> approveRequest(
            @PathVariable @Parameter(description = "Request unique identifier", example = "1", required = true) Long requestId,
            @Valid @RequestBody ResolveRequestResource resource) {
        var result = requestCommandService.handle(new ApproveRequestCommand(requestId, resource.actorId()));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RequestResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Reject a request.
     *
     * @param requestId the request id
     * @param resource the {@link ResolveRequestResource} instance, the comment is the reason
     * @return the HTTP response
     */
    @PostMapping("/{requestId}/reject")
    @Operation(summary = "Reject request", description = "Rejects an IN_PROGRESS request. The reason (comment) is required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request rejected successfully",
                    content = @Content(schema = @Schema(implementation = RequestResource.class))),
            @ApiResponse(responseCode = "400", description = "Rejection reason is missing"),
            @ApiResponse(responseCode = "404", description = "Request not found"),
            @ApiResponse(responseCode = "422", description = "Request is not IN_PROGRESS or the actor is not the assigned approver")
    })
    public ResponseEntity<?> rejectRequest(
            @PathVariable @Parameter(description = "Request unique identifier", example = "1", required = true) Long requestId,
            @Valid @RequestBody ResolveRequestResource resource) {
        var result = requestCommandService.handle(new RejectRequestCommand(requestId, resource.actorId(), resource.comment()));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RequestResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Return a request for review.
     *
     * @param requestId the request id
     * @param resource the {@link ResolveRequestResource} instance, the comment says what is missing
     * @return the HTTP response
     */
    @PostMapping("/{requestId}/return-for-review")
    @Operation(summary = "Return request for review",
            description = "Returns an IN_PROGRESS request to the requester asking for more information. The comment is required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request returned successfully",
                    content = @Content(schema = @Schema(implementation = RequestResource.class))),
            @ApiResponse(responseCode = "400", description = "Review comment is missing"),
            @ApiResponse(responseCode = "404", description = "Request not found"),
            @ApiResponse(responseCode = "422", description = "Request is not IN_PROGRESS or the actor is not the assigned approver")
    })
    public ResponseEntity<?> returnRequestForReview(
            @PathVariable @Parameter(description = "Request unique identifier", example = "1", required = true) Long requestId,
            @Valid @RequestBody ResolveRequestResource resource) {
        var result = requestCommandService.handle(
                new ReturnRequestForReviewCommand(requestId, resource.actorId(), resource.comment()));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RequestResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Resubmit a request.
     *
     * @param requestId the request id
     * @param resource the {@link ResubmitRequestResource} instance
     * @return the HTTP response
     */
    @PostMapping("/{requestId}/resubmit")
    @Operation(summary = "Resubmit request",
            description = "Sends again a request that was returned for review, with the new values and attachments. Only the requester can do it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request resubmitted successfully",
                    content = @Content(schema = @Schema(implementation = RequestResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or missing required fields"),
            @ApiResponse(responseCode = "404", description = "Request not found"),
            @ApiResponse(responseCode = "422", description = "Request is not UNDER_REVIEW or the actor is not the requester")
    })
    public ResponseEntity<?> resubmitRequest(
            @PathVariable @Parameter(description = "Request unique identifier", example = "1", required = true) Long requestId,
            @Valid @RequestBody ResubmitRequestResource resource) {
        var command = ResubmitRequestCommandFromResourceAssembler.toCommandFromResource(requestId, resource);
        var result = requestCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RequestResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Cancel a request.
     *
     * @param requestId the request id
     * @param resource the {@link ResolveRequestResource} instance
     * @return the HTTP response
     */
    @PostMapping("/{requestId}/cancel")
    @Operation(summary = "Cancel request",
            description = "Cancels a request that is not resolved yet. Only the requester can do it. Requests are never deleted, to keep the history.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request cancelled successfully",
                    content = @Content(schema = @Schema(implementation = RequestResource.class))),
            @ApiResponse(responseCode = "404", description = "Request not found"),
            @ApiResponse(responseCode = "422", description = "Request is already resolved or the actor is not the requester")
    })
    public ResponseEntity<?> cancelRequest(
            @PathVariable @Parameter(description = "Request unique identifier", example = "1", required = true) Long requestId,
            @Valid @RequestBody ResolveRequestResource resource) {
        var result = requestCommandService.handle(new CancelRequestCommand(requestId, resource.actorId()));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RequestResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    private List<RequestResource> toResources(List<Request> requests) {
        return requests.stream().map(RequestResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
}
