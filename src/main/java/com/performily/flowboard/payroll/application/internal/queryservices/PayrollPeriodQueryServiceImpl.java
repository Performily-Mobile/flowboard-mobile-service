package com.performily.flowboard.payroll.application.internal.queryservices;

import com.performily.flowboard.payroll.application.queryservices.PayrollPeriodQueryService;
import com.performily.flowboard.payroll.domain.model.entities.PayrollPeriod;
import com.performily.flowboard.payroll.domain.model.queries.GetAllPayrollPeriodsQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayrollPeriodByIdQuery;
import com.performily.flowboard.payroll.domain.repositories.PayrollPeriodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Payroll Period Query Service Impl
 * @summary
 * Application service that resolves payroll period read queries.
 *
 * @since 1.0.0
 */
@Service
public class PayrollPeriodQueryServiceImpl implements PayrollPeriodQueryService {
    private final PayrollPeriodRepository payrollPeriodRepository;

    /**
     * Constructor.
     *
     * @param payrollPeriodRepository the {@link PayrollPeriodRepository} instance
     */
    public PayrollPeriodQueryServiceImpl(PayrollPeriodRepository payrollPeriodRepository) {
        this.payrollPeriodRepository = payrollPeriodRepository;
    }

    @Override
    public Optional<PayrollPeriod> handle(GetPayrollPeriodByIdQuery query) {
        return payrollPeriodRepository.findById(query.payrollPeriodId());
    }

    @Override
    public List<PayrollPeriod> handle(GetAllPayrollPeriodsQuery query) {
        return payrollPeriodRepository.findAll();
    }
}
