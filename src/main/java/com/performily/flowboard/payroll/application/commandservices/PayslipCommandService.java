package com.performily.flowboard.payroll.application.commandservices;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.domain.model.commands.MarkPayslipAsObservedCommand;
import com.performily.flowboard.payroll.domain.model.commands.MarkPayslipAsPaidCommand;
import com.performily.flowboard.payroll.domain.model.commands.PublishPayrollPeriodPayslipsCommand;
import com.performily.flowboard.payroll.domain.model.commands.PublishPayslipCommand;
import com.performily.flowboard.payroll.domain.model.commands.ReplacePayslipFileCommand;
import com.performily.flowboard.payroll.domain.model.commands.UploadPayslipCommand;

/**
 * Payslip Command Service
 * @summary
 * Application service contract for commands over the {@link Payslip} aggregate.
 *
 * @since 1.0.0
 */
public interface PayslipCommandService {
    /**
     * Handles the upload of a payslip.
     *
     * @param command command containing the payslip data
     * @return created payslip identifier or an application error
     */
    Result<Long, ApplicationError> handle(UploadPayslipCommand command);

    /**
     * Handles the replacement of a payslip file.
     *
     * @param command command containing the new file
     * @return updated payslip or an application error
     */
    Result<Payslip, ApplicationError> handle(ReplacePayslipFileCommand command);

    /**
     * Handles the publication of a payslip.
     *
     * @param command command containing the payslip id
     * @return updated payslip or an application error
     */
    Result<Payslip, ApplicationError> handle(PublishPayslipCommand command);

    /**
     * Handles the publication of every payslip UNDER_REVIEW of a payroll period.
     *
     * @param command command containing the payroll period id
     * @return number of published payslips or an application error
     */
    Result<Integer, ApplicationError> handle(PublishPayrollPeriodPayslipsCommand command);

    /**
     * Handles marking a payment as paid.
     *
     * @param command command containing the payslip id and the payment date
     * @return updated payslip or an application error
     */
    Result<Payslip, ApplicationError> handle(MarkPayslipAsPaidCommand command);

    /**
     * Handles marking a payment as observed.
     *
     * @param command command containing the payslip id and the reason
     * @return updated payslip or an application error
     */
    Result<Payslip, ApplicationError> handle(MarkPayslipAsObservedCommand command);
}
