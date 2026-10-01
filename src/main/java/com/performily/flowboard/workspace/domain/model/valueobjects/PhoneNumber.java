package com.performily.flowboard.workspace.domain.model.valueobjects;

/**
 * Phone Number Value Object
 * @summary
 * Optional leading +, then 7 to 15 digits. Spaces and hyphens are removed
 * before validating, so "987 654 321" is stored as "987654321".
 *
 * @param value the phone number
 * @since 1.0.0
 */
public record PhoneNumber(String value) {
    /**
     * Compact constructor for PhoneNumber.
     *
     * @throws IllegalArgumentException if the value is blank or has an invalid format
     */
    public PhoneNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or blank");
        }
        value = value.replaceAll("[\\s-]", "");
        if (!value.matches("^\\+?\\d{7,15}$")) {
            throw new IllegalArgumentException("Phone number must have an optional + followed by 7 to 15 digits");
        }
    }
}