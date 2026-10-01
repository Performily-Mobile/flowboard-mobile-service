package com.performily.flowboard.workspace.application.commandservices;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import com.performily.flowboard.workspace.domain.model.commands.*;
import com.performily.flowboard.workspace.domain.model.entities.EmployeeDocument;

/**
 * Employee Command Service
 * @summary
 * Application service contract for commands over the {@link Employee} aggregate.
 *
 * @since 1.0.0
 */
public interface EmployeeCommandService {
    /**
     * Handles employee registration.
     *
     * @param command command containing the employee data
     * @return registered employee identifier or an application error
     */
    Result<Long, ApplicationError> handle(RegisterEmployeeCommand command);

    /**
     * Handles the personal data update.
     *
     * @param command command containing the employee id and the new data
     * @return updated employee or an application error
     */
    Result<Employee, ApplicationError> handle(UpdateEmployeePersonalDataCommand command);

    /**
     * Handles the area and position change.
     *
     * @param command command containing the employee id, area, position and effective date
     * @return updated employee or an application error
     */
    Result<Employee, ApplicationError> handle(AssignJobCommand command);

    /**
     * Handles the direct manager assignment.
     *
     * @param command command containing the employee id and the manager id
     * @return updated employee or an application error
     */
    Result<Employee, ApplicationError> handle(AssignDirectManagerCommand command);

    /**
     * Handles the direct manager removal.
     *
     * @param command command containing the employee id
     * @return updated employee or an application error
     */
    Result<Employee, ApplicationError> handle(RemoveDirectManagerCommand command);

    /**
     * Handles employee suspension.
     *
     * @param command command containing the employee id
     * @return updated employee or an application error
     */
    Result<Employee, ApplicationError> handle(SuspendEmployeeCommand command);

    /**
     * Handles employee termination.
     *
     * @param command command containing the employee id and the termination details
     * @return updated employee or an application error
     */
    Result<Employee, ApplicationError> handle(TerminateEmployeeCommand command);

    /**
     * Handles employee reinstatement.
     *
     * @param command command containing the employee id, area, position and reinstatement date
     * @return updated employee or an application error
     */
    Result<Employee, ApplicationError> handle(ReinstateEmployeeCommand command);

    /**
     * Handles the document attachment.
     *
     * @param command command containing the employee id and the file data
     * @return attached document or an application error
     */
    Result<EmployeeDocument, ApplicationError> handle(AttachEmployeeDocumentCommand command);
}