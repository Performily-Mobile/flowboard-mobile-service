package com.performily.flowboard.payroll.domain.model.valueobjects;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Pay Period
 * @summary
 * Month of the year a payroll belongs to.
 *
 * Rules: month between 1 and 12, year from 2000 and it cannot be a future month.
 *
 * @param year  the year
 * @param month the month, from 1 to 12
 * @since 1.0.0
 */
public record PayPeriod(int year, int month) {
    private static final int MIN_YEAR = 2000;
    private static final Locale SPANISH = Locale.forLanguageTag("es-PE");

    /**
     * Compact constructor for PayPeriod.
     *
     * @throws IllegalArgumentException if a rule is broken
     */
    public PayPeriod {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year < MIN_YEAR) {
            throw new IllegalArgumentException("Year must be %d or later".formatted(MIN_YEAR));
        }
        if (YearMonth.of(year, month).isAfter(YearMonth.now())) {
            throw new IllegalArgumentException("Pay period cannot be a future month");
        }
    }

    /**
     * Creates a pay period from a {@link YearMonth}.
     *
     * @param yearMonth the year and month
     * @return the pay period
     */
    public static PayPeriod of(YearMonth yearMonth) {
        if (yearMonth == null) {
            throw new IllegalArgumentException("Year and month are required");
        }
        return new PayPeriod(yearMonth.getYear(), yearMonth.getMonthValue());
    }

    /**
     * Converts the pay period to a {@link YearMonth}.
     *
     * @return the year and month
     */
    public YearMonth toYearMonth() {
        return YearMonth.of(year, month);
    }

    /**
     * Readable name of the period, for example "Agosto 2026".
     *
     * @return the label
     */
    public String label() {
        var monthName = toYearMonth().getMonth().getDisplayName(TextStyle.FULL, SPANISH);
        return monthName.substring(0, 1).toUpperCase(SPANISH) + monthName.substring(1) + " " + year;
    }
}
