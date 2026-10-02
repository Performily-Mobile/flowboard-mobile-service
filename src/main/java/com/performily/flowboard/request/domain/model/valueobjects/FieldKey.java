package com.performily.flowboard.request.domain.model.valueobjects;

import java.util.regex.Pattern;

/**
 * Field Key Value Object
 * @summary
 * Key of a dynamic field. Starts with a lower case letter, followed by 1 to 49
 * letters or digits (camelCase), for example "reason" or "medicalCenter".
 * It is unique inside its request type.
 *
 * @param value the key
 * @since 1.0.0
 */
public record FieldKey(String value) {
    private static final Pattern KEY_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9]{1,49}$");

    /**
     * Compact constructor for FieldKey.
     *
     * @throws IllegalArgumentException if the value does not match the pattern
     */
    public FieldKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Field key cannot be null or blank");
        }
        value = value.trim();
        if (!KEY_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Field key '%s' must start with a lower case letter and have 2 to 50 letters or digits".formatted(value));
        }
    }
}
