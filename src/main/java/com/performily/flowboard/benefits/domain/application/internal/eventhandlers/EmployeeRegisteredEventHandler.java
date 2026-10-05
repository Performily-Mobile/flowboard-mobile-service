package com.performily.flowboard.benefits.application.internal.eventhandlers;

import com.performily.flowboard.benefits.application.commandservices.VacationBalanceCommandService;
import com.performily.flowboard.benefits.domain.model.commands.OpenVacationBalanceCommand;
import com.performily.flowboard.workspace.domain.model.events.EmployeeRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Employee Registered Event Handler
 * @summary
 * When Workspace registers an employee, Benefits opens its vacation balance with
 * the days earned since the hire date.
 *
 * @since 1.0.0
 */
@Component("benefitsEmployeeRegisteredEventHandler")
public class EmployeeRegisteredEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeRegisteredEventHandler.class);

    private final VacationBalanceCommandService vacationBalanceCommandService;

    public EmployeeRegisteredEventHandler(VacationBalanceCommandService vacationBalanceCommandService) {
        this.vacationBalanceCommandService = vacationBalanceCommandService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(EmployeeRegisteredEvent event) {
        var result = vacationBalanceCommandService.handle(new OpenVacationBalanceCommand(event.employeeId()));
        if (result.isFailure()) {
            LOGGER.warn("Vacation balance of employee {} was not opened", event.employeeId());
        }
    }
}
