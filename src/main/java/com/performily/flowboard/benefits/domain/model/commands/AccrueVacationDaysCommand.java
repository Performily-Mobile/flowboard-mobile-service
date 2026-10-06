package com.performily.flowboard.benefits.domain.model.commands;

import java.time.LocalDate;

/**
 * Adds the monthly accrual to every active employee (US41). Sent by the monthly scheduler.
 *
 * @param accrualDate any date of the month to accrue
 */
public record AccrueVacationDaysCommand(LocalDate accrualDate) {
}
