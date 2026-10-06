package com.performily.flowboard.payroll.application.internal.commandservices;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.payroll.application.commandservices.PayrollPeriodCommandService;
import com.performily.flowboard.payroll.domain.model.commands.CreatePayrollPeriodCommand;
import com.performily.flowboard.payroll.domain.model.entities.PayrollPeriod;
import com.performily.flowboard.payroll.domain.model.valueobjects.PayPeriod;
import com.performily.flowboard.payroll.domain.repositories.PayrollPeriodRepository;
import org.springframework.stereotype.Service;

/**
 * Payroll Period Command Service Impl
 * @summary
 * Application service that executes payroll period commands.
 *
 * @since 1.0.0
 */
@Service
public class PayrollPeriodCommandServiceImpl implements PayrollPeriodCommandService {
    private final PayrollPeriodRepository payrollPeriodRepository;

    /**
     * Constructor.
     *
     * @param payrollPeriodRepository the {@link PayrollPeriodRepository} instance
     */
    public PayrollPeriodCommandServiceImpl(PayrollPeriodRepository payrollPeriodRepository) {
        this.payrollPeriodRepository = payrollPeriodRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(CreatePayrollPeriodCommand command) {
        try {
            var period = new PayPeriod(command.year(), command.month());
            if (payrollPeriodRepository.existsByYearAndMonth(period.year(), period.month()))
                return Result.failure(ApplicationError.conflict("PayrollPeriod",
                        "Period %s already exists".formatted(period.label())));
            var payrollPeriod = payrollPeriodRepository.save(new PayrollPeriod(period, command.scheduledPaymentDate()));
            return Result.success(payrollPeriod.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("payroll-period", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-payroll-period", e.getMessage()));
        }
    }
}
