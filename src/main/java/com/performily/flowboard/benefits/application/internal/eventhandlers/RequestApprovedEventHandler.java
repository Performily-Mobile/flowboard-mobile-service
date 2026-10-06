package com.performily.flowboard.benefits.application.internal.eventhandlers;

import com.performily.flowboard.benefits.application.commandservices.VacationBalanceCommandService;
import com.performily.flowboard.benefits.domain.model.aggregates.VacationBalance;
import com.performily.flowboard.benefits.domain.model.commands.UseVacationDaysCommand;
import com.performily.flowboard.request.domain.model.events.RequestApprovedEvent;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

/**
 * Request Approved Event Handler
 * @summary
 * When Request approves a request whose type deducts VACATION_DAYS, Benefits takes
 * the days from the balance of the requester (US41, scenario 2).
 *
 * It runs after the approval is committed and in its own transaction: a problem in
 * Benefits never undoes the approval in Request. The debit is idempotent, so a
 * repeated event does not take the days twice.
 *
 * @since 1.0.0
 */
@Component
public class RequestApprovedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestApprovedEventHandler.class);
    private static final String VACATION_DAYS = "VACATION_DAYS";

    private final VacationBalanceCommandService vacationBalanceCommandService;

    public RequestApprovedEventHandler(VacationBalanceCommandService vacationBalanceCommandService) {
        this.vacationBalanceCommandService = vacationBalanceCommandService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(RequestApprovedEvent event) {
        if (!VACATION_DAYS.equals(event.balanceDeduction()) || event.requestedDays() <= 0) {
            return;
        }
        var result = vacationBalanceCommandService.handle(new UseVacationDaysCommand(
                event.requesterId(), BigDecimal.valueOf(event.requestedDays()), event.requestId()));
        if (result instanceof Result.Failure<VacationBalance, ApplicationError> failure) {
            var error = failure.error();
            LOGGER.warn("Vacation days of request {} were not debited: {}", event.requestId(),
                    error.details() != null ? error.details() : error.message());
        }
    }
}
