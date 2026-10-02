package com.performily.flowboard.request.domain.model.valueobjects;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Field Data Type
 * @summary
 * Data type of dynamic field of a request type. Each type knows whether a
 * text value is valid for it.
 *
 * @since 1.0.0
 */
public enum FieldDataType {
    TEXT,
    NUMBER,
    DATE,
    TIME,
    MONEY,
    BOOLEAN;

    /**
     * Checks whether the given text is a valid value for this data type.
     * DATE uses yyyy-MM-dd, TIME uses HH:mm, MONEY accepts up to 2 decimals and no
     * negative amounts, BOOLEAN accepts true or false.
     *
     * @param value the value to check
     * @return true if the value is valid
     */
    public boolean accepts(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        var trimmed = value.trim();
        try {
            return switch (this) {
                case TEXT -> true;
                case NUMBER -> {
                    new BigDecimal(trimmed);
                    yield true;
                }
                case DATE -> {
                    LocalDate.parse(trimmed);
                    yield true;
                }
                case TIME -> {
                    LocalTime.parse(trimmed);
                    yield true;
                }
                case MONEY -> {
                    var amount = new BigDecimal(trimmed);
                    yield amount.signum() >= 0 && amount.scale() <= 2;
                }
                case BOOLEAN -> trimmed.equalsIgnoreCase("true") || trimmed.equalsIgnoreCase("false");
            };
        } catch (NumberFormatException | DateTimeParseException e) {
            return false;
        }
    }
}
