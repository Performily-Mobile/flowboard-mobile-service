package com.performily.flowboard.request.domain.model.commands;

import com.performily.flowboard.request.domain.model.entities.RequestField;
import com.performily.flowboard.request.domain.model.valueobjects.BalanceDeduction;

import java.util.List;

/**
 * Update Request Type Command
 * @summary
 * Command to update a request type. When fields is given it replaces the fields of
 * the form (matched by key); when it is null the fields are kept.
 *
 * @since 1.0.0
 */
public record UpdateRequestTypeCommand(Long requestTypeId, String name, String description, boolean requiresAttachment,
                                       BalanceDeduction balanceDeduction, List<RequestField> fields) {
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
        fields = fields == null ? null : List.copyOf(fields);
    }
}
