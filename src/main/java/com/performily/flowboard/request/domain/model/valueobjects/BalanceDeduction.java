package com.performily.flowboard.request.domain.model.valueobjects;

/**
 * Balance Deduction
 * @summary
 * Indicates which balance a request type deducts when it is approved.
 *
 * @since 1.0.0
 */
public enum BalanceDeduction {
    NONE,
    VACATION_DAYS,
    BENEFIT_BALANCE
}
