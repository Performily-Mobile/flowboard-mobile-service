package com.performily.flowboard.workspace.application.internal.commandservices;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.domain.model.valueobjects.EmailAddress;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;
import com.performily.flowboard.workspace.application.commandservices.EmployeeCommandService;
import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import com.performily.flowboard.workspace.domain.model.commands.*;
import com.performily.flowboard.workspace.domain.model.entities.Area;
import com.performily.flowboard.workspace.domain.model.entities.EmployeeDocument;
import com.performily.flowboard.workspace.domain.model.entities.Position;
import com.performily.flowboard.workspace.domain.model.valueobjects.*;
import com.performily.flowboard.workspace.domain.repositories.AreaRepository;
import com.performily.flowboard.workspace.domain.repositories.EmployeeRepository;
import com.performily.flowboard.workspace.domain.repositories.PositionRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.function.Consumer;

/**
 * Employee Command Service Impl
 * @summary
 * Application service that executes employee commands.
 *
 * Besides calling the aggregate, it checks the invariants that need other
 * employees: unique identity document among ACTIVE employees, no cycles in the
 * hierarchy and no termination while the employee still has subordinates.
 *
 * @since 1.0.0
 */
@Service
public class EmployeeCommandServiceImpl implements EmployeeCommandService {
    private final EmployeeRepository employeeRepository;
    private final AreaRepository areaRepository;
    private final PositionRepository positionRepository;

    /**
     * Constructor.
     *
     * @param employeeRepository the {@link EmployeeRepository} instance
     * @param areaRepository the {@link AreaRepository} instance
     * @param positionRepository the {@link PositionRepository} instance
     */
    public EmployeeCommandServiceImpl(EmployeeRepository employeeRepository,
                                      AreaRepository areaRepository,
                                      PositionRepository positionRepository) {
        this.employeeRepository = employeeRepository;
        this.areaRepository = areaRepository;
        this.positionRepository = positionRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(RegisterEmployeeCommand command) {
        if (command.directManagerId() != null) {
            var manager = employeeRepository.findById(command.directManagerId());
            if (manager.isEmpty())
                return Result.failure(ApplicationError.notFound("Employee", command.directManagerId().toString()));
            if (!manager.get().isActive())
                return Result.failure(ApplicationError.businessRuleViolation(
                        "direct-manager", "The direct manager must be an active employee"));
        }
        return findJob(command.areaId(), command.positionId())
                .flatMap(job -> register(command, job))
                .flatMap(employeeId -> command.directManagerId() == null
                        ? Result.success(employeeId)
                        : update(employeeId, "assign-direct-manager", employee ->
                                employee.assignDirectManager(new EmployeeId(command.directManagerId())))
                        .map(Employee::getId));
    }

    @Override
    public Result<Employee, ApplicationError> handle(UpdateEmployeePersonalDataCommand command) {
        try {
            var email = new EmailAddress(command.email());
            if (employeeRepository.existsByEmailAndIdIsNot(email, command.employeeId()))
                return Result.failure(ApplicationError.conflict("Employee",
                        "Email '%s' already exists".formatted(email.value())));
            return update(command.employeeId(), "update-employee-personal-data", employee ->
                    employee.updatePersonalData(
                            new PersonName(command.firstName(), command.lastName()),
                            new BirthDate(command.birthDate()),
                            new ContactInfo(email, new PhoneNumber(command.phoneNumber())),
                            toAddress(command.street(), command.district(), command.province(), command.department())));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("employee", e.getMessage()));
        }
    }

    @Override
    public Result<Employee, ApplicationError> handle(AssignJobCommand command) {
        return findJob(command.areaId(), command.positionId())
                .flatMap(job -> update(command.employeeId(), "assign-job", employee ->
                        employee.assignJob(job.area(), job.position(), command.effectiveDate())));
    }

    @Override
    public Result<Employee, ApplicationError> handle(AssignDirectManagerCommand command) {
        var manager = employeeRepository.findById(command.managerId());
        if (manager.isEmpty())
            return Result.failure(ApplicationError.notFound("Employee", command.managerId().toString()));
        if (!manager.get().isActive())
            return Result.failure(ApplicationError.businessRuleViolation(
                    "direct-manager", "The direct manager must be an active employee"));
        if (createsCycle(command.employeeId(), command.managerId()))
            return Result.failure(ApplicationError.businessRuleViolation(
                    "direct-manager", "The hierarchy cannot contain cycles"));
        return update(command.employeeId(), "assign-direct-manager", employee ->
                employee.assignDirectManager(new EmployeeId(command.managerId())));
    }

    @Override
    public Result<Employee, ApplicationError> handle(RemoveDirectManagerCommand command) {
        return update(command.employeeId(), "remove-direct-manager", Employee::removeDirectManager);
    }

    @Override
    public Result<Employee, ApplicationError> handle(SuspendEmployeeCommand command) {
        return update(command.employeeId(), "suspend-employee", Employee::suspend);
    }

    @Override
    public Result<Employee, ApplicationError> handle(TerminateEmployeeCommand command) {
        if (employeeRepository.existsByDirectManagerIdAndStatusNot(command.employeeId(), EmploymentStatus.TERMINATED))
            return Result.failure(ApplicationError.businessRuleViolation(
                    "employee-termination", "An employee with subordinates cannot be terminated until they are reassigned"));
        return update(command.employeeId(), "terminate-employee", employee ->
                employee.terminate(new TerminationDetails(command.reason(), command.terminationDate())));
    }

    @Override
    public Result<Employee, ApplicationError> handle(ReinstateEmployeeCommand command) {
        var employee = employeeRepository.findById(command.employeeId());
        if (employee.isEmpty())
            return Result.failure(ApplicationError.notFound("Employee", command.employeeId().toString()));
        if (employeeRepository.existsByIdentityDocumentAndStatus(employee.get().getIdentityDocument(), EmploymentStatus.ACTIVE))
            return Result.failure(ApplicationError.conflict("Employee",
                    "Identity document already belongs to an active employee"));
        return findJob(command.areaId(), command.positionId())
                .flatMap(job -> update(command.employeeId(), "reinstate-employee", found ->
                        found.reinstate(job.area(), job.position(), command.reinstatementDate())));
    }

    @Override
    public Result<EmployeeDocument, ApplicationError> handle(AttachEmployeeDocumentCommand command) {
        try {
            var file = new FileReference(command.fileName(), command.contentType(), command.sizeInBytes(), command.storageUrl());
            return update(command.employeeId(), "attach-employee-document",
                    employee -> employee.attachDocument(command.documentType(), file))
                    .map(saved -> saved.getDocuments().stream()
                            .max(Comparator.comparing(EmployeeDocument::getId))
                            .orElseThrow());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("employee-document", e.getMessage()));
        }
    }

    /**
     * Registers the employee once the area and the position have been found.
     */
    private Result<Long, ApplicationError> register(RegisterEmployeeCommand command, AreaAndPosition job) {
        try {
            var identityDocument = new IdentityDocument(command.identityDocumentType(), command.identityDocumentNumber());
            if (employeeRepository.existsByIdentityDocumentAndStatus(identityDocument, EmploymentStatus.ACTIVE))
                return Result.failure(ApplicationError.conflict("Employee",
                        "Identity document '%s' already belongs to an active employee".formatted(identityDocument.number())));
            var email = new EmailAddress(command.email());
            if (employeeRepository.existsByEmail(email))
                return Result.failure(ApplicationError.conflict("Employee",
                        "Email '%s' already exists".formatted(email.value())));
            var employee = new Employee(
                    new PersonName(command.firstName(), command.lastName()),
                    identityDocument,
                    new BirthDate(command.birthDate()),
                    new ContactInfo(email, new PhoneNumber(command.phoneNumber())),
                    toAddress(command.street(), command.district(), command.province(), command.department()),
                    command.contractType(),
                    new EmploymentPeriod(command.hireDate(), command.contractEndDate()),
                    job.area(),
                    job.position());
            return Result.success(employeeRepository.save(employee).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("employee", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("register-employee", e.getMessage()));
        }
    }

    /**
     * Loads the employee, applies the change and saves it, translating domain
     * exceptions into application errors.
     */
    private Result<Employee, ApplicationError> update(Long employeeId, String context, Consumer<Employee> change) {
        var result = employeeRepository.findById(employeeId);
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Employee", employeeId.toString()));
        var employee = result.get();
        try {
            change.accept(employee);
            return Result.success(employeeRepository.save(employee));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("employee", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation(context, e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected(context, e.getMessage()));
        }
    }

    /**
     * Builds the address, or returns null when every field is empty.
     * A partially filled address is rejected by the Address value object.
     *
     * @param street the street
     * @param district the district
     * @param province the province
     * @param department the department
     * @return the {@link Address}, or null when it was not provided
     */
    private static Address toAddress(String street, String district, String province, String department) {
        if (isBlank(street) && isBlank(district) && isBlank(province) && isBlank(department)) {
            return null;
        }
        return new Address(street, district, province, department);
    }

    /**
     * Checks whether a text is null or blank.
     *
     * @param value the text
     * @return true when the text is null or blank
     */
    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Finds the area and the position used by an assignment.
     */
    private Result<AreaAndPosition, ApplicationError> findJob(Long areaId, Long positionId) {
        var area = areaRepository.findById(areaId);
        if (area.isEmpty())
            return Result.failure(ApplicationError.notFound("Area", areaId.toString()));
        var position = positionRepository.findById(positionId);
        if (position.isEmpty())
            return Result.failure(ApplicationError.notFound("Position", positionId.toString()));
        return Result.success(new AreaAndPosition(area.get(), position.get()));
    }

    /**
     * Checks whether assigning the manager would create a cycle, walking up
     * the hierarchy from the manager.
     */
    private boolean createsCycle(Long employeeId, Long managerId) {
        var visited = new HashSet<Long>();
        Long currentId = managerId;
        while (currentId != null && visited.add(currentId)) {
            if (currentId.equals(employeeId)) return true;
            currentId = employeeRepository.findById(currentId)
                    .map(Employee::getDirectManagerId)
                    .map(EmployeeId::value)
                    .orElse(null);
        }
        return currentId != null;
    }

    private record AreaAndPosition(Area area, Position position) {
    }
}