package com.performily.flowboard.iam.application.internal.eventhandlers;

import com.performily.flowboard.iam.application.internal.commandservices.IamService;
import com.performily.flowboard.iam.domain.model.aggregates.UserAccount;
import com.performily.flowboard.workspace.domain.model.events.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.*;

@Component
public class EmployeeEventsHandler {

    private final IamService service;

    public EmployeeEventsHandler(IamService s) {
        service = s;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registered(EmployeeRegisteredEvent e) {
        service.registered(e.employeeId(), e.email());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void terminated(EmployeeTerminatedEvent e) {
        service.status(e.employeeId(), UserAccount.Status.DISABLED);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reinstated(EmployeeReinstatedEvent e) {
        service.status(e.employeeId(), UserAccount.Status.ACTIVE);
    }
}