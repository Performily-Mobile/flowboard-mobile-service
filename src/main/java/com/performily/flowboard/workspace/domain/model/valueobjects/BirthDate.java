package com.performily.flowboard.workspace.domain.model.valueobjects;

import java.time.LocalDate;
import java.time.Period;

/**
 * Birth Date Value Object
 * @summary
 * Required. Cannot be after today.
 *
 * @param value the birth date
 * @since 1.0.0
 */
public record BirthDate(LocalDate value) {
    /**
     * Compact constructor for BirthDate.
     *
     * @throws IllegalArgumentException if the value is null or after today
     */
    public BirthDate {
        if (value == null) {
            throw new IllegalArgumentException("Birth date is required");
        }
        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be after today");
        }
    }

    /**
     * Calculates the age on the given date.
     *
     * @param date the reference date
     * @return the age in complete years
     */
    public int ageOn(LocalDate date) {
        return Period.between(value, date).getYears();
    }
}