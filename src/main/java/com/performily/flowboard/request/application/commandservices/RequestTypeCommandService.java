package com.performily.flowboard.request.application.commandservices;

import com.performily.flowboard.request.domain.model.commands.*;
import com.performily.flowboard.request.domain.model.entities.RequestType;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;

/**
 * Request Type Command Service
 * @summary
 * Application service contract for the commands over the request type catalog.
 *
 * @since 1.0.0
 */
public interface RequestTypeCommandService {
    /**
     * Handles the creation of a request type.
     *
     * @param command command containing the request type data
     * @return created request type identifier or an application error
     */
    Result<Long, ApplicationError> handle(CreateRequestTypeCommand command);

    /**
     * Handles the update of a request type.
     *
     * @param command command containing the new data
     * @return updated request type or an application error
     */
    Result<RequestType, ApplicationError> handle(UpdateRequestTypeCommand command);

    /**
     * Handles adding a field to a request type.
     *
     * @param command command containing the field data
     * @return updated request type or an application error
     */
    Result<RequestType, ApplicationError> handle(AddRequestFieldCommand command);

    /**
     * Handles removing a field from a request type.
     *
     * @param command command containing the request type id and the field id
     * @return updated request type or an application error
     */
    Result<RequestType, ApplicationError> handle(RemoveRequestFieldCommand command);

    /**
     * Handles the activation of a request type.
     *
     * @param command command containing the request type id
     * @return updated request type or an application error
     */
    Result<RequestType, ApplicationError> handle(ActivateRequestTypeCommand command);

    /**
     * Handles the deactivation of a request type.
     *
     * @param command command containing the request type id
     * @return updated request type or an application error
     */
    Result<RequestType, ApplicationError> handle(DeactivateRequestTypeCommand command);

    /**
     * Handles the deletion of a request type without requests.
     *
     * @param command command containing the request type id
     * @return deleted request type identifier or an application error
     */
    Result<Long, ApplicationError> handle(DeleteRequestTypeCommand command);
}
