package com.performily.flowboard.benefits.application.internal.commandservices;

import com.performily.flowboard.benefits.application.commandservices.VacationBalanceCommandService;
import com.performily.flowboard.benefits.application.internal.outboundservices.acl.ExternalWorkspaceService;
import com.performily.flowboard.benefits.domain.model.aggregates.VacationBalance;
import com.performily.flowboard.benefits.domain.model.commands.*;
import com.performily.flowboard.benefits.domain.model.valueobjects.VacationDays;
import com.performily.flowboard.benefits.domain.repositories.VacationBalanceRepository;
import com.performily.flowboard.benefits.domain.services.VacationAccrualPolicy;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.shared.domain.model.valueobjects.RequestId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Vacation Balance Command Service Implementation
 * @summary
 * Executes the commands of the vacation balance (US41 and US42): opening, monthly
 * accrual, usage when a request is approved, reversal when it is annulled and
 * manual adjustment by HR staff.
 *
 * Usage and reversal are idempotent: if the same request arrives twice, the second
 * time nothing changes. That makes the event handlers safe if an event is repeated.
 *
 * @since 1.0.0
 */
@Service
public class VacationBalanceCommandServiceImpl implements VacationBalanceCommandService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VacationBalanceCommandServiceImpl.class);

    private final VacationBalanceRepository vacationBalanceRepository;
    private final ExternalWorkspaceService externalWorkspaceService;
    private final VacationAccrualPolicy accrualPolicy = new VacationAccrualPolicy();

    public VacationBalanceCommandServiceImpl(VacationBalanceRepository vacationBalanceRepository,
                                             ExternalWorkspaceService externalWorkspaceService) {
        this.vacationBalanceRepository = vacationBalanceRepository;
        this.externalWorkspaceService = externalWorkspaceService;
    }

    @Override
    @Transactional
    public Result<VacationBalance, ApplicationError> handle(OpenVacationBalanceCommand command) {
        var existing = vacationBalanceRepository.findByEmployeeId(command.employeeId());
        if (existing.isPresent()) {
            return Result.success(existing.get());
        }
        var employee = externalWorkspaceService.fetchEmployeeById(command.employeeId());
        if (employee.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Employee", String.valueOf(command.employeeId())));
        }
        if (!employee.get().active()) {
            return Result.failure(ApplicationError.businessRuleViolation("active-employee",
                    "Vacation balances are opened only for ACTIVE employees"));
        }
        var today = LocalDate.now();
        var initialAccrual = accrualPolicy.initialAccrual(employee.get().hireDate(), today);
        var balance = VacationBalance.open(new EmployeeId(command.employeeId()), initialAccrual, today);
        return Result.success(vacationBalanceRepository.save(balance));
    }

    // US41 - Monthly accrual of every active employee
    @Override
    @Transactional
    public Result<Integer, ApplicationError> handle(AccrueVacationDaysCommand command) {
        var accrualDate = command.accrualDate() != null ? command.accrualDate() : LocalDate.now();
        int accrued = 0;
        for (var employee : externalWorkspaceService.fetchAllActiveEmployees()) {
            try {
                var balance = vacationBalanceRepository.findByEmployeeId(employee.id());
                if (balance.isEmpty()) {
                    handle(new OpenVacationBalanceCommand(employee.id()));
                    continue;
                }
                if (accrualPolicy.accruesInMonthOf(employee.hireDate(), accrualDate)
                        && balance.get().accrueMonthly(accrualPolicy.monthlyAccrual(), accrualDate)) {
                    vacationBalanceRepository.save(balance.get());
                    accrued++;
                }
            } catch (RuntimeException e) {
                LOGGER.warn("Monthly accrual failed for employee {}: {}", employee.id(), e.getMessage());
            }
        }
        return Result.success(accrued);
    }

    // US41 scenario 2 - An approved vacation request takes its days
    @Override
    @Transactional
    public Result<VacationBalance, ApplicationError> handle(UseVacationDaysCommand command) {
        if (command.requestId() == null) {
            return Result.failure(ApplicationError.validationError("requestId", "Request id is required"));
        }
        var balance = vacationBalanceRepository.findByEmployeeId(command.employeeId());
        if (balance.isEmpty()) {
            return Result.failure(ApplicationError.notFound("VacationBalance", String.valueOf(command.employeeId())));
        }
        var requestId = new RequestId(command.requestId());
        if (balance.get().hasUsageFor(requestId)) {
            return Result.success(balance.get());
        }
        try {
            balance.get().debit(new VacationDays(command.days()), requestId);
            return Result.success(vacationBalanceRepository.save(balance.get()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("vacation-balance", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("days", e.getMessage()));
        }
    }

    // US41 scenario 3 - An annulled approved request returns its days
    @Override
    @Transactional
    public Result<VacationBalance, ApplicationError> handle(ReverseVacationUsageCommand command) {
        if (command.requestId() == null) {
            return Result.failure(ApplicationError.validationError("requestId", "Request id is required"));
        }
        var balance = vacationBalanceRepository.findByEmployeeId(command.employeeId());
        if (balance.isEmpty()) {
            return Result.failure(ApplicationError.notFound("VacationBalance", String.valueOf(command.employeeId())));
        }
        var requestId = new RequestId(command.requestId());
        if (!balance.get().hasUsageFor(requestId) || balance.get().hasReversalFor(requestId)) {
            return Result.success(balance.get());
        }
        balance.get().reverseUsageOf(requestId);
        return Result.success(vacationBalanceRepository.save(balance.get()));
    }

    // US42 - Manual adjustment with reason and author
    @Override
    @Transactional
    public Result<VacationBalance, ApplicationError> handle(AdjustVacationBalanceCommand command) {
        if (command.authorId() == null) {
            return Result.failure(ApplicationError.validationError("authorId", "The author of the adjustment is required"));
        }
        var balance = vacationBalanceRepository.findByEmployeeId(command.employeeId());
        if (balance.isEmpty()) {
            return Result.failure(ApplicationError.notFound("VacationBalance", String.valueOf(command.employeeId())));
        }
        if (externalWorkspaceService.fetchEmployeeById(command.authorId()).isEmpty()) {
            return Result.failure(ApplicationError.notFound("Employee", String.valueOf(command.authorId())));
        }
        try {
            balance.get().adjust(command.days(), command.reason(), new EmployeeId(command.authorId()));
            return Result.success(vacationBalanceRepository.save(balance.get()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("vacation-balance", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("adjustment", e.getMessage()));
        }
    }
}
