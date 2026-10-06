package com.performily.flowboard.payroll.application.internal.commandservices;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;
import com.performily.flowboard.shared.domain.model.valueobjects.Money;
import com.performily.flowboard.payroll.application.commandservices.PayslipCommandService;
import com.performily.flowboard.payroll.application.internal.outboundservices.acl.ExternalWorkspaceService;
import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.domain.model.commands.MarkPayslipAsObservedCommand;
import com.performily.flowboard.payroll.domain.model.commands.MarkPayslipAsPaidCommand;
import com.performily.flowboard.payroll.domain.model.commands.PublishPayrollPeriodPayslipsCommand;
import com.performily.flowboard.payroll.domain.model.commands.PublishPayslipCommand;
import com.performily.flowboard.payroll.domain.model.commands.ReplacePayslipFileCommand;
import com.performily.flowboard.payroll.domain.model.commands.UploadPayslipCommand;
import com.performily.flowboard.payroll.domain.model.valueobjects.PublicationStatus;
import com.performily.flowboard.payroll.domain.repositories.PayrollPeriodRepository;
import com.performily.flowboard.payroll.domain.repositories.PayslipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.function.Consumer;

/**
 * Payslip Command Service Impl
 * @summary
 * Application service that executes payslip commands.
 *
 * @since 1.0.0
 */
@Service
public class PayslipCommandServiceImpl implements PayslipCommandService {
    private final PayslipRepository payslipRepository;
    private final PayrollPeriodRepository payrollPeriodRepository;
    private final ExternalWorkspaceService externalWorkspaceService;

    /**
     * Constructor.
     *
     * @param payslipRepository        the {@link PayslipRepository} instance
     * @param payrollPeriodRepository  the {@link PayrollPeriodRepository} instance
     * @param externalWorkspaceService the {@link ExternalWorkspaceService} instance
     */
    public PayslipCommandServiceImpl(PayslipRepository payslipRepository,
                                     PayrollPeriodRepository payrollPeriodRepository,
                                     ExternalWorkspaceService externalWorkspaceService) {
        this.payslipRepository = payslipRepository;
        this.payrollPeriodRepository = payrollPeriodRepository;
        this.externalWorkspaceService = externalWorkspaceService;
    }

    @Override
    public Result<Long, ApplicationError> handle(UploadPayslipCommand command) {
        if (externalWorkspaceService.fetchEmployeeById(command.employeeId()).isEmpty())
            return Result.failure(ApplicationError.notFound("Employee", command.employeeId().toString()));
        var payrollPeriod = payrollPeriodRepository.findById(command.payrollPeriodId());
        if (payrollPeriod.isEmpty())
            return Result.failure(ApplicationError.notFound("PayrollPeriod", command.payrollPeriodId().toString()));
        if (payslipRepository.existsByEmployeeIdAndPayrollPeriodId(command.employeeId(), command.payrollPeriodId()))
            return Result.failure(ApplicationError.conflict("Payslip",
                    "The employee already has a payslip for this period. Replace the existing file instead."));
        try {
            var payslip = new Payslip(
                    new EmployeeId(command.employeeId()),
                    payrollPeriod.get(),
                    new FileReference(command.fileName(), command.contentType(), command.sizeInBytes(), command.storageUrl()),
                    command.issueDate(),
                    toMoney(command.netAmount(), command.currency()));
            return Result.success(payslipRepository.save(payslip).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("payslip", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("upload-payslip", e.getMessage()));
        }
    }

    @Override
    public Result<Payslip, ApplicationError> handle(ReplacePayslipFileCommand command) {
        return update(command.payslipId(), "replace-payslip-file", payslip -> payslip.replaceFile(
                new FileReference(command.fileName(), command.contentType(), command.sizeInBytes(), command.storageUrl()),
                command.issueDate(),
                toMoney(command.netAmount(), command.currency())));
    }

    @Override
    public Result<Payslip, ApplicationError> handle(PublishPayslipCommand command) {
        return update(command.payslipId(), "publish-payslip", Payslip::publish);
    }

    @Override
    @Transactional
    public Result<Integer, ApplicationError> handle(PublishPayrollPeriodPayslipsCommand command) {
        if (payrollPeriodRepository.findById(command.payrollPeriodId()).isEmpty())
            return Result.failure(ApplicationError.notFound("PayrollPeriod", command.payrollPeriodId().toString()));
        try {
            var pending = payslipRepository.findAllByPayrollPeriodId(command.payrollPeriodId()).stream()
                    .filter(payslip -> payslip.getPublicationStatus() == PublicationStatus.UNDER_REVIEW)
                    .toList();
            pending.forEach(payslip -> {
                payslip.publish();
                payslipRepository.save(payslip);
            });
            return Result.success(pending.size());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("publish-payroll-period-payslips", e.getMessage()));
        }
    }

    @Override
    public Result<Payslip, ApplicationError> handle(MarkPayslipAsPaidCommand command) {
        return update(command.payslipId(), "mark-payslip-as-paid", payslip -> payslip.markAsPaid(command.paidOn()));
    }

    @Override
    public Result<Payslip, ApplicationError> handle(MarkPayslipAsObservedCommand command) {
        return update(command.payslipId(), "mark-payslip-as-observed", payslip -> payslip.markAsObserved(command.reason()));
    }

    /**
     * Loads a payslip, applies a change and saves it, translating the domain
     * exceptions into application errors.
     */
    private Result<Payslip, ApplicationError> update(Long payslipId, String operation, Consumer<Payslip> change) {
        var result = payslipRepository.findById(payslipId);
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Payslip", payslipId.toString()));
        var payslip = result.get();
        try {
            change.accept(payslip);
            return Result.success(payslipRepository.save(payslip));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("payslip", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation(operation, e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected(operation, e.getMessage()));
        }
    }

    private static Money toMoney(BigDecimal amount, String currency) {
        return currency == null || currency.isBlank()
                ? new Money(amount)
                : new Money(amount, Currency.getInstance(currency.trim().toUpperCase()));
    }
}
