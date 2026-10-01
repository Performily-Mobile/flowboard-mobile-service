package com.performily.flowboard.workspace.domain.model.commands;

import com.performily.flowboard.workspace.domain.model.valueobjects.ContractType;
import com.performily.flowboard.workspace.domain.model.valueobjects.IdentityDocumentType;

import java.time.LocalDate;

/**
 * Register Employee Command
 * @summary
 * Command to register a new employee. contractEndDate is optional, except for FIXED_TERM contracts.
 *
 * @since 1.0.0
 */
public record RegisterEmployeeCommand(
        String firstName,
        String lastName,
        IdentityDocumentType identityDocumentType,
        String identityDocumentNumber,
        LocalDate birthDate,
        String email,
        String phoneNumber,
        String street,
        String district,
        String province,
        String department,
        ContractType contractType,
        LocalDate hireDate,
        LocalDate contractEndDate,
        Long areaId,
        Long positionId) {
    /**
     * Compact constructor for RegisterEmployeeCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public RegisterEmployeeCommand {
        if (identityDocumentType == null) {
            throw new IllegalArgumentException("identityDocumentType cannot be null");
        }
        if (contractType == null) {
            throw new IllegalArgumentException("contractType cannot be null");
        }
        if (hireDate == null) {
            throw new IllegalArgumentException("hireDate cannot be null");
        }
        if (areaId == null || areaId <= 0) {
            throw new IllegalArgumentException("areaId cannot be null or less than 1");
        }
        if (positionId == null || positionId <= 0) {
            throw new IllegalArgumentException("positionId cannot be null or less than 1");
        }
    }
}