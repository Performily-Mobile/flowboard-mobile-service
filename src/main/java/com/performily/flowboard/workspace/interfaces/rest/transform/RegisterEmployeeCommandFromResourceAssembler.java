package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.RegisterEmployeeCommand;
import com.performily.flowboard.workspace.domain.model.valueobjects.ContractType;
import com.performily.flowboard.workspace.domain.model.valueobjects.IdentityDocumentType;
import com.performily.flowboard.workspace.interfaces.rest.resources.RegisterEmployeeResource;

import java.util.Locale;

/**
 * Register Employee Command From Resource Assembler
 * @summary
 * Assembler to convert a RegisterEmployeeResource to a RegisterEmployeeCommand.
 *
 * @since 1.0.0
 */
public class RegisterEmployeeCommandFromResourceAssembler {
    /**
     * Converts a {@link RegisterEmployeeResource} to a {@link RegisterEmployeeCommand}.
     *
     * @param resource the {@link RegisterEmployeeResource} instance
     * @return the {@link RegisterEmployeeCommand}
     */
    public static RegisterEmployeeCommand toCommandFromResource(RegisterEmployeeResource resource) {
        return new RegisterEmployeeCommand(
                resource.firstName(),
                resource.lastName(),
                IdentityDocumentType.valueOf(resource.identityDocumentType().trim().toUpperCase(Locale.ROOT)),
                resource.identityDocumentNumber(),
                resource.birthDate(),
                resource.email(),
                resource.phoneNumber(),
                resource.street(),
                resource.district(),
                resource.province(),
                resource.department(),
                ContractType.valueOf(resource.contractType().trim().toUpperCase(Locale.ROOT)),
                resource.hireDate(),
                resource.contractEndDate(),
                resource.areaId(),
                resource.positionId());
    }
}