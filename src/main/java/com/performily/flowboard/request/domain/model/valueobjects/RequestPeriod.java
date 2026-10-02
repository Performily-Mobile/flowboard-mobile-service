package com.performily.flowboard.request.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Request Period Value Object
 * @summary
 * Period requested. It is optional in the request (for example an employment
 * certificate does not need it). When it exists:
 * - startDate and endDate are required and endDate cannot be before startDate.
 * - startTime and endTime go together; when they are given the period must be a
 *   single day and endTime must be after startTime (for example a permission of hours).
 *
 * @param startDate the start date
 * @param endDate   the end date
 * @param startTime the start time, optional
 * @param endTime   the end time, optional
 * @since 1.0.0
 */
public record RequestPeriod(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
    /**
     * Compact constructor for RequestPeriod.
     *
     * @throws IllegalArgumentException if a rule is not met
     */
    public RequestPeriod {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        if ((startTime == null) != (endTime == null)) {
            throw new IllegalArgumentException("Start time and end time must be given together");
        }
        if (startTime != null) {
            if (!startDate.equals(endDate)) {
                throw new IllegalArgumentException("A period with hours must start and end on the same day");
            }
            if (!endTime.isAfter(startTime)) {
                throw new IllegalArgumentException("End time must be after start time");
            }
        }
    }

    /**
     * Creates a period of whole days.
     *
     * @param startDate the start date
     * @param endDate   the end date
     */
    public RequestPeriod(LocalDate startDate, LocalDate endDate) {
        this(startDate, endDate, null, null);
    }

    /**
     * Creates a period only when there is a start or end date. Used by the
     * application layer because the period is optional.
     *
     * @return the period, or null when no date was given
     */
    public static RequestPeriod ofNullable(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        if (startDate == null && endDate == null && startTime == null && endTime == null) {
            return null;
        }
        return new RequestPeriod(startDate, endDate, startTime, endTime);
    }

    /**
     * Number of calendar days of the period, both dates included.
     * A period of hours counts as 0 days.
     *
     * @return the number of days
     */
    public int days() {
        if (hasHours()) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * Number of hours of the period. Only for periods with hours.
     *
     * @return the hours with 2 decimals, or 0 when the period has no hours
     */
    public BigDecimal hours() {
        if (!hasHours()) {
            return BigDecimal.ZERO;
        }
        var minutes = Duration.between(startTime, endTime).toMinutes();
        return BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    /**
     * Checks whether the period has start and end times.
     *
     * @return true if it has hours
     */
    public boolean hasHours() {
        return startTime != null;
    }

    /**
     * Checks whether this period shares at least one day with another period.
     *
     * @param other the other period
     * @return true if they overlap
     */
    public boolean overlaps(RequestPeriod other) {
        if (other == null) {
            return false;
        }
        return !startDate.isAfter(other.endDate) && !other.startDate.isAfter(endDate);
    }
}
