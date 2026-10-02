package com.performily.flowboard.request.application.commandservices;

import com.performily.flowboard.request.domain.model.aggregates.Request;
import com.performily.flowboard.request.domain.model.commands.*;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;

/**
 * Request Command Service
 * @summary
 * Application service contract for the commands of the request flow.
 *
 * @since 1.0.0
 */
public interface RequestCommandService {
    /**
     * Handles the submission of a request.
     *
     * @param command command containing the request data
     * @return created request identifier or an application error
     */
    Result<Long, ApplicationError> handle(SubmitRequestCommand command);

    /**
     * Handles the approval of a request.
     *
     * @param command command containing the request id and the approver
     * @return updated request or an application error
     */
    Result<Request, ApplicationError> handle(ApproveRequestCommand command);

    /**
     * Handles the rejection of a request.
     *
     * @param command command containing the request id, the approver and the reason
     * @return updated request or an application error
     */
    Result<Request, ApplicationError> handle(RejectRequestCommand command);

    /**
     * Handles the return of a request for review.
     *
     * @param command command containing the request id, the approver and the comment
     * @return updated request or an application error
     */
    Result<Request, ApplicationError> handle(ReturnRequestForReviewCommand command);

    /**
     * Handles the resubmission of a request returned for review.
     *
     * @param command command containing the request id, the requester and the new values
     * @return updated request or an application error
     */
    Result<Request, ApplicationError> handle(ResubmitRequestCommand command);

    /**
     * Handles the cancellation of a request by its requester.
     *
     * @param command command containing the request id and the requester
     * @return updated request or an application error
     */
    Result<Request, ApplicationError> handle(CancelRequestCommand command);
}
