package com.performily.flowboard.benefits.domain.model.valueobjects;

/**
 * Vacation Movement Type
 * @summary
 * Reason of a change in a vacation balance.
 * <ul>
 *   <li>ACCRUAL: days earned by seniority (initial and monthly accrual).</li>
 *   <li>USAGE: days taken by an approved vacation request.</li>
 *   <li>REVERSAL: days returned when an approved request is annulled.</li>
 *   <li>MANUAL_ADJUSTMENT: correction made by HR staff, with reason and author.</li>
 * </ul>
 *
 * @since 1.0.0
 */
public enum VacationMovementType {
    ACCRUAL,
    USAGE,
    REVERSAL,
    MANUAL_ADJUSTMENT
}
