package com.performily.flowboard.workspace.domain.model.valueobjects;

import java.time.LocalDate;

/**
 * Employment Period Value Object
 * @summary
 * hireDate is required. contractEndDate is optional, but it cannot be before
 * hireDate. The FIXED_TERM rule is checked by the Employee aggregate, because it
 * depends on the contract type.
 *
 * @param hireDate        the hire date
 * @param contractEndDate the contract end date, or null when open-ended
 * @since 1.0.0
 */
public record EmploymentPeriod(LocalDate hireDate, LocalDate contractEndDate) {
    /**
     * Compact constructor for EmploymentPeriod.
     *
     * @throws IllegalArgumentException if hireDate is null or contractEndDate is before hireDate
     */
    public EmploymentPeriod {
        if (hireDate == null) {
            throw new IllegalArgumentException("Hire date is required");
        }
        if (contractEndDate != null && contractEndDate.isBefore(hireDate)) {
            throw new IllegalArgumentException("Contract end date cannot be before hire date");
        }
    }

    /**
     * Checks whether the period has no contract end date.
     *
     * @return true if the period is open-ended
     */
    public boolean isOpenEnded() {
        return contractEndDate == null;
    }
}