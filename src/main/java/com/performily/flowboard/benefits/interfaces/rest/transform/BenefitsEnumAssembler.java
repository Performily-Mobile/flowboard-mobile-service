package com.performily.flowboard.benefits.interfaces.rest.transform;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Converts text values of the requests into enums. An invalid value becomes an
 * IllegalArgumentException, which the global handler answers with 400 and the
 * allowed values, instead of a generic error.
 */
public final class BenefitsEnumAssembler {
    private BenefitsEnumAssembler() {
    }

    public static <E extends Enum<E>> E toEnum(Class<E> type, String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            var allowed = Arrays.stream(type.getEnumConstants()).map(Enum::name).collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Invalid value '%s' for %s. Allowed values: %s".formatted(value, fieldName, allowed));
        }
    }
}
