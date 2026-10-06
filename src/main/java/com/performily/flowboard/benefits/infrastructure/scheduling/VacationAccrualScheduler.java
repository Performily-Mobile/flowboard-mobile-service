package com.performily.flowboard.benefits.infrastructure.scheduling;

import com.performily.flowboard.benefits.application.commandservices.VacationBalanceCommandService;
import com.performily.flowboard.benefits.domain.model.commands.AccrueVacationDaysCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Vacation Accrual Scheduler
 * @summary
 * On the first day of each month adds 2.5 vacation days to every active employee
 * (US41). The accrual is idempotent per month, so running it again the same month
 * does not add the days twice.
 *
 * The schedule can be changed with the property benefits.vacations.accrual-cron.
 *
 * @since 1.0.0
 */
@Component
public class VacationAccrualScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(VacationAccrualScheduler.class);

    private final VacationBalanceCommandService vacationBalanceCommandService;

    public VacationAccrualScheduler(VacationBalanceCommandService vacationBalanceCommandService) {
        this.vacationBalanceCommandService = vacationBalanceCommandService;
    }

    @Scheduled(cron = "${benefits.vacations.accrual-cron:0 10 0 1 * *}")
    public void runMonthlyAccrual() {
        var result = vacationBalanceCommandService.handle(new AccrueVacationDaysCommand(LocalDate.now()));
        result.toOptional().ifPresent(count -> LOGGER.info("Monthly vacation accrual applied to {} balances", count));
    }
}
