package com.performily.flowboard.payroll.application.queryservices;

import com.performily.flowboard.payroll.domain.model.entities.PayrollPeriod;
import com.performily.flowboard.payroll.domain.model.queries.GetAllPayrollPeriodsQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayrollPeriodByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Payroll Period Query Service
 * @summary
 * Application service contract for payroll period read queries.
 *
 * @since 1.0.0
 */
public interface PayrollPeriodQueryService {
    /**
     * Handles retrieval of a payroll period by id.
     *
     * @param query payroll-period-id query
     * @return matching payroll period, if found
     */
    Optional<PayrollPeriod> handle(GetPayrollPeriodByIdQuery query);

    /**
     * Handles retrieval of every payroll period.
     *
     * @param query all-periods query
     * @return list of payroll periods, the most recent first
     */
    List<PayrollPeriod> handle(GetAllPayrollPeriodsQuery query);
}
