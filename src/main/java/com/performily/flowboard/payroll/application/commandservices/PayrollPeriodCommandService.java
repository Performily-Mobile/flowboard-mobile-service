package com.performily.flowboard.payroll.application.commandservices;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.payroll.domain.model.commands.CreatePayrollPeriodCommand;

/**
 * Payroll Period Command Service
 * @summary
 * Application service contract for payroll period commands.
 *
 * @since 1.0.0
 */
public interface PayrollPeriodCommandService {
    /**
     * Handles the creation of a payroll period.
     *
     * @param command command containing the period data
     * @return created payroll period identifier or an application error
     */
    Result<Long, ApplicationError> handle(CreatePayrollPeriodCommand command);
}
