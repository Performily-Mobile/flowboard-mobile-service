package com.performily.flowboard.workspace.application.facades;

import com.performily.flowboard.workspace.domain.model.aggregates.Employee; 
import com.performily.flowboard.workspace.domain.model.valueobjects.EmploymentStatus; 
import com.performily.flowboard.workspace.domain.repositories.EmployeeRepository; 
import org.springframework.stereotype.Component; 

import java.util.List; 
import java.util.Optional;

@Component 
public class WorkspaceContextFacadeImpl implements WorkspaceContextFacade { 
    
    private final EmployeeRepository employeeRepository; 

    public WorkspaceContextFacadeImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    } 

    public Optional<Employee> findEmployeeById(Long id) {
        return employeeRepository.findById(id);
    } 

    public List<Employee> findActiveEmployeesByAreaId(Long areaId) {
        return employeeRepository.findAllByFilters(null, areaId, EmploymentStatus.ACTIVE, null);
    } 

    public List<Employee> findAllActiveEmployees() {
        return employeeRepository.findAllByStatusNot(EmploymentStatus.TERMINATED).stream()
            .filter(e -> e.getStatus() == EmploymentStatus.ACTIVE)
            .toList();
    } 
}