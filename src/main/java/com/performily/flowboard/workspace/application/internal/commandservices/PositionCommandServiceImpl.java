package com.performily.flowboard.workspace.application.internal.commandservices;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.domain.model.valueobjects.Money;
import com.performily.flowboard.workspace.application.commandservices.PositionCommandService;
import com.performily.flowboard.workspace.domain.model.commands.CreatePositionCommand;
import com.performily.flowboard.workspace.domain.model.commands.DeactivatePositionCommand;
import com.performily.flowboard.workspace.domain.model.commands.UpdatePositionReferenceSalaryCommand;
import com.performily.flowboard.workspace.domain.model.entities.Position;
import com.performily.flowboard.workspace.domain.repositories.AreaRepository;
import com.performily.flowboard.workspace.domain.repositories.PositionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

/**
 * Position Command Service Impl
 * @summary
 * Application service that executes position commands.
 *
 * @since 1.0.0
 */
@Service
public class PositionCommandServiceImpl implements PositionCommandService {
    private final PositionRepository positionRepository;
    private final AreaRepository areaRepository;

    /**
     * Constructor.
     *
     * @param positionRepository the {@link PositionRepository} instance
     * @param areaRepository the {@link AreaRepository} instance
     */
    public PositionCommandServiceImpl(PositionRepository positionRepository, AreaRepository areaRepository) {
        this.positionRepository = positionRepository;
        this.areaRepository = areaRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(CreatePositionCommand command) {
        var area = areaRepository.findById(command.areaId());
        if (area.isEmpty())
            return Result.failure(ApplicationError.notFound("Area", command.areaId().toString()));
        if (!area.get().isActive())
            return Result.failure(ApplicationError.businessRuleViolation(
                    "position-creation", "Positions can only be created in active areas"));
        try {
            var referenceSalary = toMoney(command.referenceSalaryAmount(), command.referenceSalaryCurrency());
            var position = positionRepository.save(new Position(command.title(), area.get(), referenceSalary));
            return Result.success(position.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("position", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-position", e.getMessage()));
        }
    }

    @Override
    public Result<Position, ApplicationError> handle(UpdatePositionReferenceSalaryCommand command) {
        var result = positionRepository.findById(command.positionId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Position", command.positionId().toString()));
        var position = result.get();
        try {
            position.updateReferenceSalary(toMoney(command.referenceSalaryAmount(), command.referenceSalaryCurrency()));
            return Result.success(positionRepository.save(position));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("position", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("update-position-reference-salary", e.getMessage()));
        }
    }

    @Override
    public Result<Position, ApplicationError> handle(DeactivatePositionCommand command) {
        var result = positionRepository.findById(command.positionId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Position", command.positionId().toString()));
        var position = result.get();
        try {
            position.deactivate();
            return Result.success(positionRepository.save(position));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("position-deactivation", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("deactivate-position", e.getMessage()));
        }
    }

    private static Money toMoney(BigDecimal amount, String currencyCode) {
        var currency = (currencyCode == null || currencyCode.isBlank())
                ? Money.DEFAULT_CURRENCY
                : Currency.getInstance(currencyCode.trim().toUpperCase(Locale.ROOT));
        return new Money(amount, currency);
    }
}