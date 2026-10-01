package com.performily.flowboard.workspace.domain.model.valueobjects;

import java.time.LocalDate;

/**
 * Termination Details Value Object
 * @summary
 * reason: not blank, max 500. terminationDate is required. The rule
 * "not before the hire date" is checked by the Employee aggregate.
 *
 * @param reason          the termination reason
 * @param terminationDate the termination date
 * @since 1.0.0
 */
public record TerminationDetails(String reason, LocalDate terminationDate) {
    private static final int REASON_MAX_LENGTH = 500;

    /**
     * Compact constructor for TerminationDetails.
     *
     * @throws IllegalArgumentException if the reason is blank or too long, or the date is null
     */
    public TerminationDetails {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Termination reason cannot be null or blank");
        }
        reason = reason.trim();
        if (reason.length() > REASON_MAX_LENGTH) {
            throw new IllegalArgumentException("Termination reason cannot exceed %d characters".formatted(REASON_MAX_LENGTH));
        }
        if (terminationDate == null) {
            throw new IllegalArgumentException("Termination date is required");
        }
    }
}