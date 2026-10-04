package com.performily.flowboard.request.domain.model.valueobjects;

import java.util.Objects;

/**
 * Field Value Object
 * @summary
 * Value entered by the requester for one dynamic field of the request type.
 * The value is stored as text (max 500 characters) and is checked against the
 * data type of the field by the request type.
 *
 * @param key   the field key
 * @param value the value
 * @since 1.0.0
 */
public record FieldValue(FieldKey key, String value) {
    private static final int VALUE_MAX_LENGTH = 500;

    /**
     * Compact constructor for FieldValue.
     *
     * @throws IllegalArgumentException if the key is null or the value is too long
     */
    public FieldValue {
        Objects.requireNonNull(key, "Field value key cannot be null");
        if (value != null) {
            value = value.trim();
            if (value.length() > VALUE_MAX_LENGTH) {
                throw new IllegalArgumentException(
                        "Value of field '%s' cannot exceed %d characters".formatted(key.value(), VALUE_MAX_LENGTH));
            }
        }
    }

    /**
     * Creates a field value from plain text.
     *
     * @param key   the field key
     * @param value the value
     */
    public FieldValue(String key, String value) {
        this(new FieldKey(key), value);
    }

    /**
     * Checks whether the value is empty.
     *
     * @return true if the value is null or blank
     */
    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
