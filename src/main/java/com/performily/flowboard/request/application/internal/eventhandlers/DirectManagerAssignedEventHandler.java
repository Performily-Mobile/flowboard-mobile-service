package com.performily.flowboard.request.application.internal.eventhandlers;

import com.performily.flowboard.request.domain.model.valueobjects.Approver;
import com.performily.flowboard.request.domain.repositories.RequestRepository;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.workspace.domain.model.events.DirectManagerAssignedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Direct Manager Assigned Event Handler
 * @summary
 * Listens to the DirectManagerAssignedEvent of Workspace and moves the requests of
 * the employee that are not resolved yet to the new direct manager.
 *
 * @since 1.0.0
 */
@Component
public class DirectManagerAssignedEventHandler {
    private final RequestRepository requestRepository;

    /**
     * Constructor.
     *
     * @param requestRepository the {@link RequestRepository} instance
     */
    public DirectManagerAssignedEventHandler(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Reassigns the approver of the pending requests of the employee.
     *
     * @param event the {@link DirectManagerAssignedEvent} instance
     */
    @EventListener
    public void on(DirectManagerAssignedEvent event) {
        if (event.employeeId() == null || event.managerId() == null) {
            return;
        }
        var newApprover = Approver.directManager(new EmployeeId(event.managerId()));
        requestRepository.findAllUnresolvedByRequesterId(event.employeeId()).forEach(request -> {
            request.reassignApprover(newApprover);
            requestRepository.save(request);
        });
    }
}
