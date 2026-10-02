package com.performily.flowboard.attendance.application.internal.eventhandlers;

import com.performily.flowboard.workspace.domain.model.events.EmployeeTerminatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Handles employee termination events from Workspace.
 * Future daily closing operations only enumerate ACTIVE employees, so no new
 * ABSENT records are generated after termination. Existing attendance history remains immutable.
 */
@Component
public class EmployeeTerminatedEventHandler {
    
    @EventListener
    public void on(EmployeeTerminatedEvent event) {
        // Integration hook intentionally kept side-effect free: attendance history is retained.
    }
}