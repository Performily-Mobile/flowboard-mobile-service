package com.performily.flowboard.shared.domain.model.valueobjects;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Date Range Value Object
 * @summary
 * Closed range of dates: both startDate and endDate are included.
 *
 * Rules: startDate and endDate are required and endDate cannot be before startDate.
 *
 * @param startDate the first day of the range
 * @param endDate   the last day of the range
 * @since 1.0.0
 */
public record DateRange(LocalDate startDate, LocalDate endDate) {
    /**
     * Compact constructor for DateRange.
     *
     * @throws IllegalArgumentException if a date is missing or endDate is before startDate
     */
    public DateRange {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    /**
     * Checks whether the range includes a date.
     *
     * @param date the date to check
     * @return true when startDate <= date <= endDate
     */
    public boolean contains(LocalDate date) {
        return date != null && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Checks whether two ranges share at least one day.
     *
     * @param other the other range
     * @return true when both ranges have at least one day in common
     */
    public boolean overlaps(DateRange other) {
        return other != null && !startDate.isAfter(other.endDate) && !other.startDate.isAfter(endDate);
    }

    /**
     * Number of days of the range, both ends included.
     *
     * @return the length of the range in days
     */
    public long lengthInDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
}
