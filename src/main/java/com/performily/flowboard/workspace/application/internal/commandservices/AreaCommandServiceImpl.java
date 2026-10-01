package com.performily.flowboard.workspace.application.internal.commandservices;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.workspace.application.commandservices.AreaCommandService;
import com.performily.flowboard.workspace.domain.model.commands.ActivateAreaCommand;
import com.performily.flowboard.workspace.domain.model.commands.CreateAreaCommand;
import com.performily.flowboard.workspace.domain.model.commands.DeactivateAreaCommand;
import com.performily.flowboard.workspace.domain.model.commands.RenameAreaCommand;
import com.performily.flowboard.workspace.domain.model.entities.Area;
import com.performily.flowboard.workspace.domain.model.valueobjects.EmploymentStatus;
import com.performily.flowboard.workspace.domain.repositories.AreaRepository;
import com.performily.flowboard.workspace.domain.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

/**
 * Area Command Service Impl
 * @summary
 * Application service that executes area commands.
 *
 * @since 1.0.0
 */
@Service
public class AreaCommandServiceImpl implements AreaCommandService {
    private final AreaRepository areaRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Constructor.
     *
     * @param areaRepository the {@link AreaRepository} instance
     * @param employeeRepository the {@link EmployeeRepository} instance
     */
    public AreaCommandServiceImpl(AreaRepository areaRepository, EmployeeRepository employeeRepository) {
        this.areaRepository = areaRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(CreateAreaCommand command) {
        if (areaRepository.existsByName(command.name().trim()))
            return Result.failure(ApplicationError.conflict("Area", "Name '%s' already exists".formatted(command.name())));
        try {
            var area = areaRepository.save(new Area(command.name(), command.description()));
            return Result.success(area.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("area", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-area", e.getMessage()));
        }
    }

    @Override
    public Result<Area, ApplicationError> handle(RenameAreaCommand command) {
        if (areaRepository.existsByNameAndIdIsNot(command.name().trim(), command.areaId()))
            return Result.failure(ApplicationError.conflict("Area", "Name '%s' already exists".formatted(command.name())));
        var result = areaRepository.findById(command.areaId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Area", command.areaId().toString()));
        var area = result.get();
        try {
            area.rename(command.name());
            return Result.success(areaRepository.save(area));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("area", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("rename-area", e.getMessage()));
        }
    }

    @Override
    public Result<Area, ApplicationError> handle(ActivateAreaCommand command) {
        var result = areaRepository.findById(command.areaId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Area", command.areaId().toString()));
        var area = result.get();
        try {
            area.activate();
            return Result.success(areaRepository.save(area));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("area-activation", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("activate-area", e.getMessage()));
        }
    }

    @Override
    public Result<Area, ApplicationError> handle(DeactivateAreaCommand command) {
        var result = areaRepository.findById(command.areaId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Area", command.areaId().toString()));
        if (employeeRepository.existsByAreaIdAndStatus(command.areaId(), EmploymentStatus.ACTIVE))
            return Result.failure(ApplicationError.businessRuleViolation(
                    "area-deactivation", "An area with ACTIVE employees cannot be deactivated"));
        var area = result.get();
        try {
            area.deactivate();
            return Result.success(areaRepository.save(area));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("area-deactivation", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("deactivate-area", e.getMessage()));
        }
    }
}