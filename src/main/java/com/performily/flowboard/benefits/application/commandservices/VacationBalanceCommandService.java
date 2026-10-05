package com.performily.flowboard.benefits.application.commandservices;

import com.performily.flowboard.benefits.domain.model.aggregates.VacationBalance;
import com.performily.flowboard.benefits.domain.model.commands.*;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;

/**
 * Write operations on the vacation balances: opening, accrual, usage, reversal and adjustment.
 */
public interface VacationBalanceCommandService {
    Result<VacationBalance, ApplicationError> handle(OpenVacationBalanceCommand command);

    /**
     * @return the number of balances that received the monthly accrual
     */
    Result<Integer, ApplicationError> handle(AccrueVacationDaysCommand command);

    Result<VacationBalance, ApplicationError> handle(UseVacationDaysCommand command);

    Result<VacationBalance, ApplicationError> handle(ReverseVacationUsageCommand command);

    Result<VacationBalance, ApplicationError> handle(AdjustVacationBalanceCommand command);
}
