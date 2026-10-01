package com.performily.flowboard.shared.domain.model.valueobjects;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Email Address Value Object
 * @summary
 * Not blank, max 120 characters, valid e-mail format, stored in lower case.
 *
 * @param value the e-mail address
 * @since 1.0.0
 */
public record EmailAddress(String value) {
    private static final int MAX_LENGTH = 120;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Compact constructor for EmailAddress.
     *
     * @throws IllegalArgumentException if the value is blank, too long or has an invalid format
     */
    public EmailAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email address cannot be null or blank");
        }
        value = value.trim().toLowerCase(Locale.ROOT);
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Email address cannot exceed %d characters".formatted(MAX_LENGTH));
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Email address must have a valid format");
        }
    }

    /**
     * Gets the domain part of the e-mail address.
     *
     * @return the text after the '@' character
     */
    public String domain() {
        return value.substring(value.indexOf('@') + 1);
    }
}