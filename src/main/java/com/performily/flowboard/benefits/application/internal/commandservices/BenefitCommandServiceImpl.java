package com.performily.flowboard.benefits.application.internal.commandservices;

import com.performily.flowboard.benefits.application.commandservices.AreaAssignmentResult;
import com.performily.flowboard.benefits.application.commandservices.BenefitCommandService;
import com.performily.flowboard.benefits.application.internal.outboundservices.acl.ExternalWorkspaceService;
import com.performily.flowboard.benefits.domain.model.aggregates.BenefitAssignment;
import com.performily.flowboard.benefits.domain.model.commands.*;
import com.performily.flowboard.benefits.domain.model.entities.BenefitType;
import com.performily.flowboard.benefits.domain.model.valueobjects.AssignmentStatus;
import com.performily.flowboard.benefits.domain.model.valueobjects.BenefitQuantity;
import com.performily.flowboard.benefits.domain.repositories.BenefitAssignmentRepository;
import com.performily.flowboard.benefits.domain.repositories.BenefitTypeRepository;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.domain.model.valueobjects.AreaId;
import com.performily.flowboard.shared.domain.model.valueobjects.DateRange;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Benefit Command Service Implementation
 * @summary
 * Executes the commands of the benefit catalog (US37), the assignments (US38)
 * and the deliveries (US39).
 *
 * It checks the rules that the aggregates cannot check alone: unique names in the
 * catalog, the employee is ACTIVE in Workspace (through the anti-corruption layer)
 * and the same benefit type is not assigned twice to an employee in overlapping
 * periods. Each command runs in one transaction, so an area assignment is
 * created completely or not at all.
 *
 * @since 1.0.0
 */
@Service
public class BenefitCommandServiceImpl implements BenefitCommandService {
    private final BenefitTypeRepository benefitTypeRepository;
    private final BenefitAssignmentRepository benefitAssignmentRepository;
    private final ExternalWorkspaceService externalWorkspaceService;

    public BenefitCommandServiceImpl(BenefitTypeRepository benefitTypeRepository,
                                     BenefitAssignmentRepository benefitAssignmentRepository,
                                     ExternalWorkspaceService externalWorkspaceService) {
        this.benefitTypeRepository = benefitTypeRepository;
        this.benefitAssignmentRepository = benefitAssignmentRepository;
        this.externalWorkspaceService = externalWorkspaceService;
    }

    // US37 - Benefit catalog
    @Override
    @Transactional
    public Result<BenefitType, ApplicationError> handle(CreateBenefitTypeCommand command) {
        try {
            var benefitType = new BenefitType(command.name(), command.description(), command.hasBalance(), command.unit());
            if (benefitTypeRepository.existsByName(benefitType.getName())) {
                return Result.failure(ApplicationError.conflict("BenefitType",
                        "A benefit type named '%s' already exists".formatted(benefitType.getName())));
            }
            return Result.success(benefitTypeRepository.save(benefitType));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("benefitType", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<BenefitType, ApplicationError> handle(ActivateBenefitTypeCommand command) {
        return changeBenefitTypeStatus(command.benefitTypeId(), BenefitType::activate);
    }

    @Override
    @Transactional
    public Result<BenefitType, ApplicationError> handle(DeactivateBenefitTypeCommand command) {
        return changeBenefitTypeStatus(command.benefitTypeId(), BenefitType::deactivate);
    }

    private Result<BenefitType, ApplicationError> changeBenefitTypeStatus(Long benefitTypeId, Consumer<BenefitType> change) {
        return benefitTypeRepository.findById(benefitTypeId)
                .map(benefitType -> {
                    change.accept(benefitType);
                    return Result.<BenefitType, ApplicationError>success(benefitTypeRepository.save(benefitType));
                })
                .orElseGet(() -> Result.failure(ApplicationError.notFound("BenefitType", String.valueOf(benefitTypeId))));
    }

    // US38 scenario 1 - Assignment to one employee
    @Override
    @Transactional
    public Result<BenefitAssignment, ApplicationError> handle(AssignBenefitCommand command) {
        var benefitType = benefitTypeRepository.findById(command.benefitTypeId());
        if (benefitType.isEmpty()) {
            return Result.failure(ApplicationError.notFound("BenefitType", String.valueOf(command.benefitTypeId())));
        }
        if (!benefitType.get().isActive()) {
            return Result.failure(inactiveBenefitType(benefitType.get()));
        }
        var employee = externalWorkspaceService.fetchEmployeeById(command.employeeId());
        if (employee.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Employee", String.valueOf(command.employeeId())));
        }
        if (!employee.get().active()) {
            return Result.failure(ApplicationError.businessRuleViolation("active-employee",
                    "Benefits can only be assigned to ACTIVE employees"));
        }
        try {
            var period = new DateRange(command.startDate(), command.endDate());
            var quantity = new BenefitQuantity(command.quantity());
            if (benefitAssignmentRepository.existsByEmployeeIdAndTypeAndPeriod(command.employeeId(), command.benefitTypeId(), period)) {
                return Result.failure(ApplicationError.conflict("BenefitAssignment",
                        "%s already has '%s' in an overlapping period".formatted(employee.get().fullName(), benefitType.get().getName())));
            }
            var assignment = new BenefitAssignment(benefitType.get(), new EmployeeId(command.employeeId()), period, quantity);
            return Result.success(benefitAssignmentRepository.save(assignment));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("assignment", e.getMessage()));
        }
    }

    // US38 scenario 2 - Assignment to every active employee of an area
    @Override
    @Transactional
    public Result<AreaAssignmentResult, ApplicationError> handle(AssignBenefitToAreaCommand command) {
        var benefitType = benefitTypeRepository.findById(command.benefitTypeId());
        if (benefitType.isEmpty()) {
            return Result.failure(ApplicationError.notFound("BenefitType", String.valueOf(command.benefitTypeId())));
        }
        if (!benefitType.get().isActive()) {
            return Result.failure(inactiveBenefitType(benefitType.get()));
        }
        try {
            var period = new DateRange(command.startDate(), command.endDate());
            var quantity = new BenefitQuantity(command.quantity());
            var employeeIds = externalWorkspaceService.fetchActiveEmployeesByAreaId(command.areaId()).stream()
                    .map(ExternalWorkspaceService.WorkspaceEmployee::id).toList();
            if (employeeIds.isEmpty()) {
                return Result.failure(ApplicationError.businessRuleViolation("area-employees",
                        "Area %d has no active employees".formatted(command.areaId())));
            }
            var alreadyAssigned = benefitAssignmentRepository.findEmployeeIdsWithTypeInPeriod(employeeIds, command.benefitTypeId(), period);
            var toAssign = employeeIds.stream().filter(id -> !alreadyAssigned.contains(id)).map(EmployeeId::new).toList();
            if (toAssign.isEmpty()) {
                return Result.failure(ApplicationError.conflict("BenefitAssignment",
                        "All the active employees of the area already have '%s' in the period".formatted(benefitType.get().getName())));
            }
            var saved = new ArrayList<BenefitAssignment>();
            BenefitAssignment.forArea(benefitType.get(), new AreaId(command.areaId()), toAssign, period, quantity)
                    .forEach(assignment -> saved.add(benefitAssignmentRepository.save(assignment)));
            var skipped = alreadyAssigned.stream().sorted().toList();
            return Result.success(new AreaAssignmentResult(command.areaId(), saved, skipped));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("assignment", e.getMessage()));
        }
    }

    // US39 - Delivery registration
    @Override
    @Transactional
    public Result<BenefitAssignment, ApplicationError> handle(RegisterBenefitDeliveryCommand command) {
        var assignment = benefitAssignmentRepository.findById(command.assignmentId());
        if (assignment.isEmpty()) {
            return Result.failure(ApplicationError.notFound("BenefitAssignment", String.valueOf(command.assignmentId())));
        }
        if (command.registeredById() != null && externalWorkspaceService.fetchEmployeeById(command.registeredById()).isEmpty()) {
            return Result.failure(ApplicationError.notFound("Employee", String.valueOf(command.registeredById())));
        }
        try {
            var registeredBy = command.registeredById() == null ? null : new EmployeeId(command.registeredById());
            assignment.get().registerDelivery(command.deliveredOn(), registeredBy, command.notes());
            return Result.success(benefitAssignmentRepository.save(assignment.get()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("single-delivery", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("delivery", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<BenefitAssignment, ApplicationError> handle(CancelBenefitAssignmentCommand command) {
        var assignment = benefitAssignmentRepository.findById(command.assignmentId());
        if (assignment.isEmpty()) {
            return Result.failure(ApplicationError.notFound("BenefitAssignment", String.valueOf(command.assignmentId())));
        }
        try {
            assignment.get().cancel();
            return Result.success(benefitAssignmentRepository.save(assignment.get()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("assignment-cancellation", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public int handle(CancelPendingBenefitAssignmentsCommand command) {
        var pending = benefitAssignmentRepository.findAllByEmployeeIdAndStatus(command.employeeId(), AssignmentStatus.ASSIGNED);
        pending.forEach(assignment -> {
            assignment.cancel();
            benefitAssignmentRepository.save(assignment);
        });
        return pending.size();
    }

    private static ApplicationError inactiveBenefitType(BenefitType benefitType) {
        return ApplicationError.businessRuleViolation("active-benefit-type",
                "Benefit type '%s' is not active".formatted(benefitType.getName()));
    }
}
