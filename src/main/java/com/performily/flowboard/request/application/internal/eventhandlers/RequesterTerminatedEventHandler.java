package com.performily.flowboard.request.application.internal.eventhandlers;

import com.performily.flowboard.request.domain.repositories.RequestRepository;
import com.performily.flowboard.workspace.domain.model.events.EmployeeTerminatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Requester Terminated Event Handler
 * @summary
 * Listens to the EmployeeTerminatedEvent of Workspace and cancels the requests of the
 * terminated employee that are not resolved yet, so they do not stay in the approver inbox.
 *
 * It is not called EmployeeTerminatedEventHandler because Attendance already has a
 * bean with that name.
 *
 * @since 1.0.0
 */
@Component
public class RequesterTerminatedEventHandler {
    private static final String CANCELLATION_REASON = "Cancelled automatically: the requester was terminated";

    private final RequestRepository requestRepository;

    /**
     * Constructor.
     *
     * @param requestRepository the {@link RequestRepository} instance
     */
    public RequesterTerminatedEventHandler(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Cancels the pending requests of the terminated employee.
     *
     * @param event the {@link EmployeeTerminatedEvent} instance
     */
    @EventListener
    public void on(EmployeeTerminatedEvent event) {
        requestRepository.findAllUnresolvedByRequesterId(event.employeeId()).forEach(request -> {
            request.cancelBySystem(CANCELLATION_REASON);
            requestRepository.save(request);
        });
    }
}
