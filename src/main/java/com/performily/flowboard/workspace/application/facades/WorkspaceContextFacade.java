package com.performily.flowboard.workspace.application.facades;

import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import java.util.List;
import java.util.Optional;

/** 
 * Controlled integration point exposed by Workspace to other bounded contexts. 
 */
public interface WorkspaceContextFacade {
    
    Optional<Employee> findEmployeeById(Long employeeId);
    
    List<Employee> findActiveEmployeesByAreaId(Long areaId);
    
    List<Employee> findAllActiveEmployees();
}