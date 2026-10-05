package com.performily.flowboard.shared.domain.model.valueobjects;

/**
 * Request Id Value Object
 * @summary
 * Shared Kernel identifier for the Request concept, owned by Request.
 *
 * Benefits keeps it only as a reference inside its vacation movements,
 * never as a foreign key between the databases of both contexts.
 *
 * @param value the identifier value. It cannot be null or less than 1.
 * @since 1.0.0
 */
public record RequestId(Long value) {
    /**
     * Compact constructor for RequestId.
     *
     * @throws IllegalArgumentException if the value is null or less than 1
     */
    public RequestId {
        if (value == null || value < 1) {
            throw new IllegalArgumentException("Request id cannot be null or less than 1");
        }
    }
}
