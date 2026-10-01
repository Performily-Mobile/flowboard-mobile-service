package com.performily.flowboard.workspace.domain.model.valueobjects;

import java.util.regex.Pattern;

/**
 * Person Name Value Object
 * @summary
 * firstName: not blank, max 50. lastName: not blank, max 80.
 * Letters, spaces, apostrophes and hyphens only.
 *
 * @param firstName the first name
 * @param lastName  the last name
 * @since 1.0.0
 */
public record PersonName(String firstName, String lastName) {
    private static final int FIRST_NAME_MAX_LENGTH = 50;
    private static final int LAST_NAME_MAX_LENGTH = 80;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} '\\-]+$");

    /**
     * Compact constructor for PersonName.
     *
     * @throws IllegalArgumentException if a name is blank, too long or has invalid characters
     */
    public PersonName {
        firstName = validate(firstName, "First name", FIRST_NAME_MAX_LENGTH);
        lastName = validate(lastName, "Last name", LAST_NAME_MAX_LENGTH);
    }

    /**
     * Gets the full name.
     *
     * @return the first name followed by the last name
     */
    public String getFullName() {
        return "%s %s".formatted(firstName, lastName);
    }

    private static String validate(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("%s cannot be null or blank".formatted(field));
        }
        var trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException("%s cannot exceed %d characters".formatted(field, maxLength));
        }
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("%s can only contain letters, spaces, apostrophes and hyphens".formatted(field));
        }
        return trimmed;
    }
}