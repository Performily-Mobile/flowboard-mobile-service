package com.performily.flowboard.benefits.application.internal.queryservices;

import com.performily.flowboard.benefits.application.internal.outboundservices.acl.ExternalWorkspaceService;
import com.performily.flowboard.benefits.application.internal.outboundservices.acl.ExternalWorkspaceService.WorkspaceEmployee;
import com.performily.flowboard.benefits.application.queryservices.BenefitQueryService;
import com.performily.flowboard.benefits.application.queryservices.views.*;
import com.performily.flowboard.benefits.domain.model.aggregates.BenefitAssignment;
import com.performily.flowboard.benefits.domain.model.entities.BenefitType;
import com.performily.flowboard.benefits.domain.model.entities.VacationMovement;
import com.performily.flowboard.benefits.domain.model.queries.*;
import com.performily.flowboard.benefits.domain.repositories.BenefitAssignmentRepository;
import com.performily.flowboard.benefits.domain.repositories.BenefitTypeRepository;
import com.performily.flowboard.benefits.domain.repositories.VacationBalanceRepository;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.domain.model.valueobjects.DateRange;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Benefit Query Service Implementation
 * @summary
 * Answers the queries of the catalog, the assignments (US40) and the vacation
 * balances (US41). Names and areas of the employees are read from Workspace at
 * query time through the anti-corruption layer; Benefits never stores them.
 *
 * @since 1.0.0
 */
@Service
public class BenefitQueryServiceImpl implements BenefitQueryService {
    private final BenefitTypeRepository benefitTypeRepository;
    private final BenefitAssignmentRepository benefitAssignmentRepository;
    private final VacationBalanceRepository vacationBalanceRepository;
    private final ExternalWorkspaceService externalWorkspaceService;

    public BenefitQueryServiceImpl(BenefitTypeRepository benefitTypeRepository,
                                   BenefitAssignmentRepository benefitAssignmentRepository,
                                   VacationBalanceRepository vacationBalanceRepository,
                                   ExternalWorkspaceService externalWorkspaceService) {
        this.benefitTypeRepository = benefitTypeRepository;
        this.benefitAssignmentRepository = benefitAssignmentRepository;
        this.vacationBalanceRepository = vacationBalanceRepository;
        this.externalWorkspaceService = externalWorkspaceService;
    }

    @Override
    public List<BenefitType> handle(GetAllBenefitTypesQuery query) {
        return query.activeOnly() ? benefitTypeRepository.findAllByActiveTrue() : benefitTypeRepository.findAll();
    }

    @Override
    public Optional<BenefitType> handle(GetBenefitTypeByIdQuery query) {
        return benefitTypeRepository.findById(query.benefitTypeId());
    }

    // "Asignaciones" tab of HR staff: to deliver and delivered
    @Override
    public List<BenefitAssignmentView> handle(GetBenefitAssignmentsQuery query) {
        var assignments = benefitAssignmentRepository.findAll(query.status(), query.benefitTypeId());
        var employees = employeesById(assignments.stream().map(a -> a.getEmployeeId().value()).toList());
        return assignments.stream()
                .map(a -> new BenefitAssignmentView(a, nameOf(employees, a.getEmployeeId().value())))
                .sorted(Comparator.comparing(BenefitAssignmentView::employeeName, Comparator.nullsLast(String::compareTo)))
                .toList();
    }

    // US40 - Benefits of one employee: current and delivered
    @Override
    public Result<EmployeeBenefitsView, ApplicationError> handle(GetBenefitAssignmentsByEmployeeIdQuery query) {
        if (externalWorkspaceService.fetchEmployeeById(query.employeeId()).isEmpty()) {
            return Result.failure(ApplicationError.notFound("Employee", String.valueOf(query.employeeId())));
        }
        var today = LocalDate.now();
        var assignments = benefitAssignmentRepository.findAllByEmployeeId(query.employeeId());
        var current = assignments.stream()
                .filter(a -> a.isCurrentOn(today))
                .sorted(Comparator.comparing(a -> a.getValidity().startDate()))
                .toList();
        var delivered = assignments.stream()
                .filter(BenefitAssignment::isDelivered)
                .sorted(Comparator.comparing((BenefitAssignment a) -> a.getDelivery().getDeliveredOn()).reversed())
                .toList();
        return Result.success(new EmployeeBenefitsView(query.employeeId(), current, delivered));
    }

    // US38 - Preview before assigning to an area
    @Override
    public Result<AreaAssignmentPreview, ApplicationError> handle(GetAreaAssignmentPreviewQuery query) {
        if (benefitTypeRepository.findById(query.benefitTypeId()).isEmpty()) {
            return Result.failure(ApplicationError.notFound("BenefitType", String.valueOf(query.benefitTypeId())));
        }
        try {
            var period = new DateRange(query.startDate(), query.endDate());
            var employeeIds = externalWorkspaceService.fetchActiveEmployeesByAreaId(query.areaId()).stream()
                    .map(WorkspaceEmployee::id).toList();
            var alreadyAssigned = employeeIds.isEmpty() ? Set.<Long>of()
                    : benefitAssignmentRepository.findEmployeeIdsWithTypeInPeriod(employeeIds, query.benefitTypeId(), period);
            return Result.success(new AreaAssignmentPreview(query.areaId(), employeeIds.size(), alreadyAssigned.size(),
                    employeeIds.size() - alreadyAssigned.size()));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("period", e.getMessage()));
        }
    }

    // US41 scenario 1 - Balance with accrued, used and available days and its movements
    @Override
    public Result<VacationBalanceView, ApplicationError> handle(GetVacationBalanceByEmployeeIdQuery query) {
        var balance = vacationBalanceRepository.findByEmployeeId(query.employeeId());
        if (balance.isEmpty()) {
            return Result.failure(ApplicationError.notFound("VacationBalance", String.valueOf(query.employeeId())));
        }
        var employee = externalWorkspaceService.fetchEmployeeById(query.employeeId());
        var movements = toMovementViews(balance.get().getMovementsNewestFirst());
        return Result.success(new VacationBalanceView(balance.get(),
                employee.map(WorkspaceEmployee::fullName).orElse(null),
                employee.map(WorkspaceEmployee::areaName).orElse(null),
                movements));
    }

    @Override
    public Result<List<VacationMovementView>, ApplicationError> handle(GetVacationMovementsByEmployeeIdQuery query) {
        if (query.fromDate() != null && query.toDate() != null && query.fromDate().isAfter(query.toDate())) {
            return Result.failure(ApplicationError.validationError("dateRange", "The start date cannot be after the end date"));
        }
        var balance = vacationBalanceRepository.findByEmployeeId(query.employeeId());
        if (balance.isEmpty()) {
            return Result.failure(ApplicationError.notFound("VacationBalance", String.valueOf(query.employeeId())));
        }
        var movements = balance.get().getMovementsNewestFirst().stream()
                .filter(m -> query.fromDate() == null || !m.getOccurredAt().toLocalDate().isBefore(query.fromDate()))
                .filter(m -> query.toDate() == null || !m.getOccurredAt().toLocalDate().isAfter(query.toDate()))
                .toList();
        return Result.success(toMovementViews(movements));
    }

    // Vacation balances of the active employees, highest available days first
    @Override
    public List<VacationBalanceView> handle(GetAllVacationBalancesQuery query) {
        var employees = query.areaId() == null
                ? externalWorkspaceService.fetchAllActiveEmployees()
                : externalWorkspaceService.fetchActiveEmployeesByAreaId(query.areaId());
        var employeesById = employees.stream().collect(Collectors.toMap(WorkspaceEmployee::id, Function.identity(), (a, b) -> a));
        return vacationBalanceRepository.findAllByEmployeeIds(employeesById.keySet()).stream()
                .map(balance -> {
                    var employee = employeesById.get(balance.getEmployeeId().value());
                    return new VacationBalanceView(balance, employee.fullName(), employee.areaName(), List.of());
                })
                .sorted(Comparator.comparing((VacationBalanceView view) -> view.balance().availableDays().value()).reversed())
                .toList();
    }

    private List<VacationMovementView> toMovementViews(List<VacationMovement> movements) {
        var authors = employeesById(movements.stream()
                .map(VacationMovement::getAuthorId).filter(Objects::nonNull).map(EmployeeId::value).toList());
        return movements.stream()
                .map(m -> new VacationMovementView(m, m.getAuthorId() == null ? null : nameOf(authors, m.getAuthorId().value())))
                .toList();
    }

    private Map<Long, WorkspaceEmployee> employeesById(Collection<Long> employeeIds) {
        var employees = new HashMap<Long, WorkspaceEmployee>();
        new HashSet<>(employeeIds).forEach(id -> externalWorkspaceService.fetchEmployeeById(id)
                .ifPresent(employee -> employees.put(id, employee)));
        return employees;
    }

    private static String nameOf(Map<Long, WorkspaceEmployee> employees, Long employeeId) {
        var employee = employees.get(employeeId);
        return employee == null ? null : employee.fullName();
    }
}
