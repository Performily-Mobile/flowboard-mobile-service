package com.performily.flowboard.benefits.application.internal.eventhandlers;

import com.performily.flowboard.benefits.application.commandservices.VacationBalanceCommandService;
import com.performily.flowboard.benefits.domain.model.commands.OpenVacationBalanceCommand;
import com.performily.flowboard.workspace.domain.model.events.EmployeeReinstatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Employee Reinstated Event Handler
 * @summary
 * When an employee comes back, Benefits makes sure it has a vacation balance.
 * An existing balance is kept as it is, with its history.
 *
 * @since 1.0.0
 */
@Component("benefitsEmployeeReinstatedEventHandler")
public class EmployeeReinstatedEventHandler {
    private final VacationBalanceCommandService vacationBalanceCommandService;

    public EmployeeReinstatedEventHandler(VacationBalanceCommandService vacationBalanceCommandService) {
        this.vacationBalanceCommandService = vacationBalanceCommandService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(EmployeeReinstatedEvent event) {
        vacationBalanceCommandService.handle(new OpenVacationBalanceCommand(event.employeeId()));
    }
}
