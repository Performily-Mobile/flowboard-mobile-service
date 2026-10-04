package com.performily.flowboard.request.domain.model.commands;

import com.performily.flowboard.request.domain.model.entities.RequestField;
import com.performily.flowboard.request.domain.model.valueobjects.BalanceDeduction;

import java.util.List;

/**
 * Create Request Type Command
 * @summary
 * Command to create a request type, optionally with the fields of its form.
 *
 * @since 1.0.0
 */
public record CreateRequestTypeCommand(String name, String description, boolean requiresAttachment, BalanceDeduction balanceDeduction, List<RequestField> fields) {
    /**
     * Compact constructor for CreateRequestTypeCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public CreateRequestTypeCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or blank");
        }
        if (balanceDeduction == null) {
            throw new IllegalArgumentException("balanceDeduction cannot be null");
        }
        fields = fields == null ? List.of() : List.copyOf(fields);
    }
}
