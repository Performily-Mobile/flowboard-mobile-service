package com.performily.flowboard.workspace.application.internal.queryservices;

import com.performily.flowboard.workspace.application.queryservices.EmployeeQueryService;
import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import com.performily.flowboard.workspace.domain.model.queries.*;
import com.performily.flowboard.workspace.domain.model.valueobjects.EmploymentStatus;
import com.performily.flowboard.workspace.domain.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Employee Query Service Impl
 * @summary
 * Application service that resolves employee read queries.
 *
 * @since 1.0.0
 */
@Service
public class EmployeeQueryServiceImpl implements EmployeeQueryService {
    private final EmployeeRepository employeeRepository;

    /**
     * Constructor.
     *
     * @param employeeRepository the {@link EmployeeRepository} instance
     */
    public EmployeeQueryServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Optional<Employee> handle(GetEmployeeByIdQuery query) {
        return employeeRepository.findById(query.employeeId());
    }

    @Override
    public List<Employee> handle(GetAllEmployeesQuery query) {
        return employeeRepository.findAll();
    }

    @Override
    public List<Employee> handle(GetAllEmployeesByDirectManagerIdQuery query) {
        return employeeRepository.findAllByDirectManagerId(query.managerId());
    }

    @Override
    public List<Employee> handle(GetOrganizationChartQuery query) {
        return employeeRepository.findAllByStatusNot(EmploymentStatus.TERMINATED);
    }
}