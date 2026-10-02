package com.performily.flowboard.request.domain.model.commands;

import com.performily.flowboard.request.domain.model.valueobjects.BalanceDeduction;

/**
 * Update Request Type Command
 * @summary
 * Command to update the general data of a request type.
 *
 * @since 1.0.0
 */
public record UpdateRequestTypeCommand(Long requestTypeId, String name, String description, boolean requiresAttachment, BalanceDeduction balanceDeduction) {
    /**
     * Compact constructor for UpdateRequestTypeCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public UpdateRequestTypeCommand {
        if (requestTypeId == null || requestTypeId <= 0) {
            throw new IllegalArgumentException("requestTypeId cannot be null or less than 1");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or blank");
        }
        if (balanceDeduction == null) {
            throw new IllegalArgumentException("balanceDeduction cannot be null");
        }
    }
}
