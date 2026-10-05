package com.performily.flowboard.benefits.application.internal.eventhandlers;

import com.performily.flowboard.benefits.application.commandservices.VacationBalanceCommandService;
import com.performily.flowboard.benefits.domain.model.commands.ReverseVacationUsageCommand;
import com.performily.flowboard.request.domain.model.events.RequestCancelledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Request Cancelled Event Handler
 * @summary
 * When a request is cancelled, Benefits returns its days if they had been taken
 * (US41, scenario 3). If the request never debited days (it was cancelled before
 * being approved) nothing changes.
 *
 * It runs after the cancellation is committed and in its own transaction.
 *
 * @since 1.0.0
 */
@Component
public class RequestCancelledEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestCancelledEventHandler.class);

    private final VacationBalanceCommandService vacationBalanceCommandService;

    public RequestCancelledEventHandler(VacationBalanceCommandService vacationBalanceCommandService) {
        this.vacationBalanceCommandService = vacationBalanceCommandService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(RequestCancelledEvent event) {
        var result = vacationBalanceCommandService.handle(
                new ReverseVacationUsageCommand(event.requesterId(), event.requestId()));
        if (result.isFailure()) {
            LOGGER.debug("No vacation days to return for request {}", event.requestId());
        }
    }
}
