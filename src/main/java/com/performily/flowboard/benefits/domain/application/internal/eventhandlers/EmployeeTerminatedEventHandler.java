package com.performily.flowboard.benefits.application.internal.eventhandlers;

import com.performily.flowboard.benefits.application.commandservices.BenefitCommandService;
import com.performily.flowboard.benefits.domain.model.commands.CancelPendingBenefitAssignmentsCommand;
import com.performily.flowboard.workspace.domain.model.events.EmployeeTerminatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Employee Terminated Event Handler
 * @summary
 * Benefits are only for ACTIVE employees, so when an employee is terminated the
 * benefits not delivered yet are cancelled. Delivered benefits and the vacation
 * balance are kept as history.
 *
 * The bean has its own name because Attendance already has a class with the same name.
 *
 * @since 1.0.0
 */
@Component("benefitsEmployeeTerminatedEventHandler")
public class EmployeeTerminatedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeTerminatedEventHandler.class);

    private final BenefitCommandService benefitCommandService;

    public EmployeeTerminatedEventHandler(BenefitCommandService benefitCommandService) {
        this.benefitCommandService = benefitCommandService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(EmployeeTerminatedEvent event) {
        int cancelled = benefitCommandService.handle(new CancelPendingBenefitAssignmentsCommand(event.employeeId()));
        if (cancelled > 0) {
            LOGGER.info("{} pending benefits of employee {} were cancelled", cancelled, event.employeeId());
        }
    }
}
