package com.performily.flowboard.workspace.domain.model.valueobjects;

import java.util.Locale;

/**
 * Identity Document Value Object
 * @summary
 * DNI: exactly 8 digits. CE: 9 to 12 alphanumeric characters.
 * PASSPORT: 6 to 12 alphanumeric characters.
 *
 * @param type   the identity document type
 * @param number the identity document number
 * @since 1.0.0
 */
public record IdentityDocument(IdentityDocumentType type, String number) {
    /**
     * Compact constructor for IdentityDocument.
     *
     * @throws IllegalArgumentException if the type is null or the number does not match the type rules
     */
    public IdentityDocument {
        if (type == null) {
            throw new IllegalArgumentException("Identity document type cannot be null");
        }
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Identity document number cannot be null or blank");
        }
        number = number.trim().toUpperCase(Locale.ROOT);
        var valid = switch (type) {
            case DNI -> number.matches("^\\d{8}$");
            case CE -> number.matches("^[A-Z0-9]{9,12}$");
            case PASSPORT -> number.matches("^[A-Z0-9]{6,12}$");
        };
        if (!valid) {
            throw new IllegalArgumentException("Identity document number is not valid for type %s".formatted(type));
        }
    }
}