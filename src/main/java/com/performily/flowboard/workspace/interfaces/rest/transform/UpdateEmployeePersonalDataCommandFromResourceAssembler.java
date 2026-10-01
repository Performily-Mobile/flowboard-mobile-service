package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.UpdateEmployeePersonalDataCommand;
import com.performily.flowboard.workspace.interfaces.rest.resources.UpdateEmployeePersonalDataResource;

/**
 * Update Employee Personal Data Command From Resource Assembler
 * @summary
 * Assembler to convert an UpdateEmployeePersonalDataResource to an UpdateEmployeePersonalDataCommand.
 *
 * @since 1.0.0
 */
public class UpdateEmployeePersonalDataCommandFromResourceAssembler {
    /**
     * Converts a {@link UpdateEmployeePersonalDataResource} to a {@link UpdateEmployeePersonalDataCommand}.
     *
     * @param employeeId the employee id
     * @param resource the {@link UpdateEmployeePersonalDataResource} instance
     * @return the {@link UpdateEmployeePersonalDataCommand}
     */
    public static UpdateEmployeePersonalDataCommand toCommandFromResource(Long employeeId,
                                                                          UpdateEmployeePersonalDataResource resource) {
        return new UpdateEmployeePersonalDataCommand(
                employeeId,
                resource.firstName(),
                resource.lastName(),
                resource.birthDate(),
                resource.email(),
                resource.phoneNumber(),
                resource.street(),
                resource.district(),
                resource.province(),
                resource.department());
    }
}