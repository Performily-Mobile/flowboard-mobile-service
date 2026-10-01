package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import com.performily.flowboard.workspace.interfaces.rest.resources.EmployeeResource;

/**
 * Employee Resource From Entity Assembler
 * @summary
 * Assembler to convert an Employee aggregate to an EmployeeResource.
 *
 * @since 1.0.0
 */
public class EmployeeResourceFromEntityAssembler {
    /**
     * Converts a {@link Employee} to a {@link EmployeeResource}.
     *
     * @param entity the {@link Employee} instance
     * @return the {@link EmployeeResource}
     */
    public static EmployeeResource toResourceFromEntity(Employee entity) {
        var termination = entity.getTermination();
        var address = entity.getAddress();
        return new EmployeeResource(
                entity.getId(),
                entity.getName().firstName(),
                entity.getName().lastName(),
                entity.getFullName(),
                entity.getIdentityDocument().type().name(),
                entity.getIdentityDocument().number(),
                entity.getBirthDate().value(),
                entity.getContactInfo().email().value(),
                entity.getContactInfo().phoneNumber().value(),
                address.street(),
                address.district(),
                address.province(),
                address.department(),
                entity.getContractType().name(),
                entity.getEmploymentPeriod().hireDate(),
                entity.getEmploymentPeriod().contractEndDate(),
                entity.getStatus().name(),
                termination == null ? null : termination.reason(),
                termination == null ? null : termination.terminationDate(),
                entity.getArea().getId(),
                entity.getArea().getName(),
                entity.getPosition().getId(),
                entity.getPosition().getTitle(),
                entity.hasDirectManager() ? entity.getDirectManagerId().value() : null);
    }
}