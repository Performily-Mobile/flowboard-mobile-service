package com.performily.flowboard.benefits.infrastructure.seeding;

import com.performily.flowboard.benefits.application.commandservices.VacationBalanceCommandService;
import com.performily.flowboard.benefits.application.internal.outboundservices.acl.ExternalWorkspaceService;
import com.performily.flowboard.benefits.domain.model.commands.OpenVacationBalanceCommand;
import com.performily.flowboard.benefits.domain.repositories.VacationBalanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Opens the vacation balance of every active employee that does not have one yet,
 * for example employees registered before Benefits existed. New employees get their
 * balance through EmployeeRegisteredEventHandler.
 */
@Component
public class VacationBalanceInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(VacationBalanceInitializer.class);

    private final VacationBalanceCommandService vacationBalanceCommandService;
    private final VacationBalanceRepository vacationBalanceRepository;
    private final ExternalWorkspaceService externalWorkspaceService;

    public VacationBalanceInitializer(VacationBalanceCommandService vacationBalanceCommandService,
                                      VacationBalanceRepository vacationBalanceRepository,
                                      ExternalWorkspaceService externalWorkspaceService) {
        this.vacationBalanceCommandService = vacationBalanceCommandService;
        this.vacationBalanceRepository = vacationBalanceRepository;
        this.externalWorkspaceService = externalWorkspaceService;
    }

    /**
     * Opens a vacation balance for every active employee that does not have one yet.
     *
     * <p>Any failure is logged and swallowed: a seeding problem must never stop the application
     * from starting.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void openMissingBalances() {
        try {
            int opened = 0;
            for (var employee : externalWorkspaceService.fetchAllActiveEmployees()) {
                if (!vacationBalanceRepository.existsByEmployeeId(employee.id())
                        && vacationBalanceCommandService.handle(new OpenVacationBalanceCommand(employee.id())).isSuccess()) {
                    opened++;
                }
            }
            if (opened > 0) {
                LOGGER.info("Vacation balances opened for {} existing employees", opened);
            }
        } catch (RuntimeException exception) {
            LOGGER.warn("Vacation balances could not be opened at startup: {}", exception.getMessage(), exception);
        }
    }
}
