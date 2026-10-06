package com.performily.flowboard.benefits.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Vacation Days Value Object
 * @summary
 * Non negative amount of vacation days with at most 2 decimals,
 * because the monthly accrual is 2.5 days.
 *
 * It is immutable: every operation returns a new instance.
 *
 * @param value the number of days, always stored with scale 2
 * @since 1.0.0
 */
public record VacationDays(BigDecimal value) {
    public static final VacationDays ZERO = new VacationDays(BigDecimal.ZERO);

    /**
     * Compact constructor for VacationDays.
     *
     * @throws IllegalArgumentException if the value is null, negative or has more than 2 decimals
     */
    public VacationDays {
        if (value == null) {
            throw new IllegalArgumentException("Vacation days are required");
        }
        if (value.signum() < 0) {
            throw new IllegalArgumentException("Vacation days cannot be negative");
        }
        if (value.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Vacation days can have at most 2 decimals");
        }
        value = value.setScale(2, RoundingMode.UNNECESSARY);
    }

    /**
     * Creates vacation days from a text value, for example "2.5".
     *
     * @param value the number of days
     * @return the vacation days
     */
    public static VacationDays of(String value) {
        return new VacationDays(new BigDecimal(value));
    }

    /**
     * Creates vacation days from a whole number of days.
     *
     * @param days the number of days
     * @return the vacation days
     */
    public static VacationDays of(long days) {
        return new VacationDays(BigDecimal.valueOf(days));
    }

    public VacationDays plus(VacationDays other) {
        return new VacationDays(value.add(other.value));
    }

    /**
     * Subtracts days.
     *
     * @param other the days to subtract
     * @return the difference
     * @throws IllegalArgumentException if the result would be negative
     */
    public VacationDays minus(VacationDays other) {
        return new VacationDays(value.subtract(other.value));
    }

    public boolean isLessThan(VacationDays other) {
        return value.compareTo(other.value) < 0;
    }

    public boolean isZero() {
        return value.signum() == 0;
    }

    @Override
    public String toString() {
        return value.stripTrailingZeros().toPlainString();
    }
}
