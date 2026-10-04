package com.performily.flowboard.request.application.internal.commandservices;

import com.performily.flowboard.request.application.commandservices.RequestCommandService;
import com.performily.flowboard.request.application.internal.outboundservices.acl.ExternalWorkspaceService;
import com.performily.flowboard.request.domain.model.aggregates.Request;
import com.performily.flowboard.request.domain.model.commands.*;
import com.performily.flowboard.request.domain.model.valueobjects.Approver;
import com.performily.flowboard.request.domain.model.valueobjects.RequestPeriod;
import com.performily.flowboard.request.domain.repositories.RequestRepository;
import com.performily.flowboard.request.domain.repositories.RequestTypeRepository;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * Request Command Service Impl
 * @summary
 * Application service that executes the commands of the request flow.
 *
 * When a request is submitted it checks in Workspace that the requester exists and
 * is ACTIVE, resolves the approver (direct manager or HR staff), checks that the
 * employee has no other request in the same days, and lets the aggregate validate
 * the form against the request type.
 *
 * The balance check with Benefits (vacation days) will be added when the Benefits
 * bounded context exposes its facade.
 *
 * @since 1.0.0
 */
@Service
public class RequestCommandServiceImpl implements RequestCommandService {
    private final RequestRepository requestRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final ExternalWorkspaceService externalWorkspaceService;

    /**
     * Constructor.
     *
     * @param requestRepository the {@link RequestRepository} instance
     * @param requestTypeRepository the {@link RequestTypeRepository} instance
     * @param externalWorkspaceService the {@link ExternalWorkspaceService} instance
     */
    public RequestCommandServiceImpl(RequestRepository requestRepository,
                                     RequestTypeRepository requestTypeRepository,
                                     ExternalWorkspaceService externalWorkspaceService) {
        this.requestRepository = requestRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.externalWorkspaceService = externalWorkspaceService;
    }

    @Override
    public Result<Long, ApplicationError> handle(SubmitRequestCommand command) {
        var requester = externalWorkspaceService.fetchEmployeeById(command.requesterId());
        if (requester.isEmpty())
            return Result.failure(ApplicationError.notFound("Employee", command.requesterId().toString()));
        if (!requester.get().active())
            return Result.failure(ApplicationError.businessRuleViolation(
                    "request-submission", "Only ACTIVE employees can submit requests"));

        var requestType = requestTypeRepository.findById(command.requestTypeId());
        if (requestType.isEmpty())
            return Result.failure(ApplicationError.notFound("RequestType", command.requestTypeId().toString()));

        try {
            var period = RequestPeriod.ofNullable(command.startDate(), command.endDate(),
                    command.startTime(), command.endTime());
            if (period != null && requestRepository.existsOverlappingRequest(
                    command.requesterId(), period.startDate(), period.endDate()))
                return Result.failure(ApplicationError.conflict("Request",
                        "The employee already has a request in progress or approved for the same days"));

            var approver = requester.get().hasDirectManager()
                    ? Approver.directManager(new EmployeeId(requester.get().directManagerId()))
                    : Approver.hrStaff();

            var request = new Request(new EmployeeId(command.requesterId()), requestType.get(), period,
                    command.fieldValues(), command.attachments(), approver);
            var saved = requestRepository.save(request);
            return Result.success(saved.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("request", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("submit-request", e.getMessage()));
        }
    }

    @Override
    public Result<Request, ApplicationError> handle(ApproveRequestCommand command) {
        return execute(command.requestId(), "approve-request",
                request -> request.approve(new EmployeeId(command.actorId())));
    }

    @Override
    public Result<Request, ApplicationError> handle(RejectRequestCommand command) {
        return execute(command.requestId(), "reject-request",
                request -> request.reject(new EmployeeId(command.actorId()), command.reason()));
    }

    @Override
    public Result<Request, ApplicationError> handle(ReturnRequestForReviewCommand command) {
        return execute(command.requestId(), "return-request-for-review",
                request -> request.returnForReview(new EmployeeId(command.actorId()), command.comment()));
    }

    @Override
    public Result<Request, ApplicationError> handle(ResubmitRequestCommand command) {
        return execute(command.requestId(), "resubmit-request",
                request -> request.resubmit(new EmployeeId(command.actorId()), command.fieldValues(),
                        command.attachments(), command.comment()));
    }

    @Override
    public Result<Request, ApplicationError> handle(CancelRequestCommand command) {
        return execute(command.requestId(), "cancel-request",
                request -> request.cancel(new EmployeeId(command.actorId())));
    }

    /**
     * Loads the request, applies the change and saves it. Domain exceptions are
     * translated to application errors: IllegalArgumentException is a validation
     * error and IllegalStateException is a business rule violation.
     *
     * @param requestId the request id
     * @param action    the name of the action, used in the error messages
     * @param change    the change to apply on the aggregate
     * @return the updated request or an application error
     */
    private Result<Request, ApplicationError> execute(Long requestId, String action, Consumer<Request> change) {
        var result = requestRepository.findById(requestId);
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Request", requestId.toString()));
        var request = result.get();
        try {
            change.accept(request);
            return Result.success(requestRepository.save(request));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("request", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation(action, e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected(action, e.getMessage()));
        }
    }
}
