package com.performily.flowboard.wellbeing.domain.model.valueobjects;

/**
 * Unique inventory code of a device (e.g. ENV-0012).
 * Not blank, 4 to 30 characters, upper case letters, digits and hyphens.
 */
public record DeviceCode(String value) {
    private static final String PATTERN = "^[A-Z0-9-]{4,30}$";

    public DeviceCode {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Device code is required");
        value = value.trim().toUpperCase();
        if (!value.matches(PATTERN))
            throw new IllegalArgumentException("Device code must have 4 to 30 upper case letters, digits or hyphens");
    }
}
