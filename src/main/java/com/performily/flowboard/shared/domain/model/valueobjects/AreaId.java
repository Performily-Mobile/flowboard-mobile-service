package com.performily.flowboard.shared.domain.model.valueobjects;

/**
 * Area Id Value Object
 * @summary
 * Shared Kernel identifier for the Area concept, owned by Workspace.
 *
 * Other bounded contexts use it to refer to an area without a direct
 * dependency on the Workspace model.
 *
 * @param value the identifier value. It cannot be null or less than 1.
 * @since 1.0.0
 */
public record AreaId(Long value) {
    /**
     * Compact constructor for AreaId.
     *
     * @throws IllegalArgumentException if the value is null or less than 1
     */
    public AreaId {
        if (value == null || value < 1) {
            throw new IllegalArgumentException("Area id cannot be null or less than 1");
        }
    }
}
