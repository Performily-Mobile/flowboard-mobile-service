package com.performily.flowboard.shared.domain.model.valueobjects;

/**
 * Employee Id Value Object
 * @summary
 * Shared Kernel identifier for the Employee concept.
 *
 * Typed identifiers are the only way a bounded context refers to an
 * aggregate or entity that belongs to another bounded context.
 *
 * @param value the identifier value. It cannot be null or less than 1.
 * @since 1.0.0
 */
public record EmployeeId(Long value) {
    /**
     * Compact constructor for EmployeeId.
     *
     * @throws IllegalArgumentException if the value is null or less than 1
     */
    public EmployeeId {
        if (value == null || value < 1) {
            throw new IllegalArgumentException("Employee id cannot be null or less than 1");
        }
    }
}