package com.performily.flowboard.payroll.application.queryservices;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipByIdAndEmployeeIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipByIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipDownloadUrlQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipsByEmployeeIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipsByPayrollPeriodIdQuery;
import com.performily.flowboard.payroll.domain.model.valueobjects.PayslipDownloadLink;

import java.util.List;
import java.util.Optional;

/**
 * Payslip Query Service
 * @summary
 * Application service contract for payslip read queries.
 *
 * @since 1.0.0
 */
public interface PayslipQueryService {
    /**
     * Handles the PUBLISHED payslips of an employee for a year.
     *
     * @param query employee and year query
     * @return list of payslips, the most recent period first
     */
    List<Payslip> handle(GetPayslipsByEmployeeIdQuery query);

    /**
     * Handles a payslip of an employee. Only returns it when it is visible to that employee.
     *
     * @param query payslip and employee query
     * @return matching payslip, if visible
     */
    Optional<Payslip> handle(GetPayslipByIdAndEmployeeIdQuery query);

    /**
     * Handles the temporary download link of a payslip and records the download.
     *
     * @param query payslip and requester query
     * @return download link or an application error
     */
    Result<PayslipDownloadLink, ApplicationError> handle(GetPayslipDownloadUrlQuery query);

    /**
     * Handles any payslip by id (HR view).
     *
     * @param query payslip-id query
     * @return matching payslip, if found
     */
    Optional<Payslip> handle(GetPayslipByIdQuery query);

    /**
     * Handles every payslip of a payroll period (HR view).
     *
     * @param query payroll-period-id query
     * @return list of payslips
     */
    List<Payslip> handle(GetPayslipsByPayrollPeriodIdQuery query);
}
