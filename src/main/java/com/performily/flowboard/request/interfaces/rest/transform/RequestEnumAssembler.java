package com.performily.flowboard.request.interfaces.rest.transform;

import java.util.Arrays;
import java.util.Locale;

/**
 * Request Enum Assembler
 * @summary
 * Converts the text values received in the resources to the enumerations of the
 * Request context. An invalid value throws IllegalArgumentException, which the
 * GlobalExceptionHandler returns as 400 Bad Request.
 *
 * @since 1.0.0
 */
public final class RequestEnumAssembler {
    private RequestEnumAssembler() {
    }

    /**
     * Converts a text to a value of the given enumeration.
     *
     * @param enumType     the enumeration class
     * @param value        the text value, case insensitive
     * @param defaultValue the value used when the text is null or blank
     * @param fieldName    the name of the field, used in the error message
     * @param <E>          the enumeration type
     * @return the enumeration value
     */
    public static <E extends Enum<E>> E toEnum(Class<E> enumType, String value, E defaultValue, String fieldName) {
        if (value == null || value.isBlank()) {
            if (defaultValue == null) {
                throw new IllegalArgumentException("%s is required".formatted(fieldName));
            }
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumType, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("%s must be one of %s".formatted(
                    fieldName, Arrays.toString(enumType.getEnumConstants())));
        }
    }
}
