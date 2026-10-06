package com.performily.flowboard.payroll.domain.model.valueobjects;

import java.time.LocalDate;

/**
 * Payment Details
 * @summary
 * Payment status of a payslip.
 *
 * Rules:
 * - PENDING: no payment date and no observation reason.
 * - PAID: payment date required.
 * - OBSERVED: reason required, up to 500 characters.
 *
 * @param status            the {@link PaymentStatus}
 * @param paidOn            the payment date, only when PAID
 * @param observationReason the reason, only when OBSERVED
 * @since 1.0.0
 */
public record PaymentDetails(PaymentStatus status, LocalDate paidOn, String observationReason) {
    private static final int REASON_MAX_LENGTH = 500;

    /**
     * Compact constructor for PaymentDetails.
     *
     * @throws IllegalArgumentException if a rule is broken
     */
    public PaymentDetails {
        if (status == null) {
            throw new IllegalArgumentException("Payment status is required");
        }
        switch (status) {
            case PENDING -> {
                if (paidOn != null || observationReason != null) {
                    throw new IllegalArgumentException("A pending payment has no payment date or observation");
                }
            }
            case PAID -> {
                if (paidOn == null) {
                    throw new IllegalArgumentException("Payment date is required");
                }
                if (paidOn.isAfter(LocalDate.now())) {
                    throw new IllegalArgumentException("Payment date cannot be in the future");
                }
                observationReason = null;
            }
            case OBSERVED -> {
                if (observationReason == null || observationReason.isBlank()) {
                    throw new IllegalArgumentException("Observation reason is required");
                }
                observationReason = observationReason.trim();
                if (observationReason.length() > REASON_MAX_LENGTH) {
                    throw new IllegalArgumentException(
                            "Observation reason cannot exceed %d characters".formatted(REASON_MAX_LENGTH));
                }
                paidOn = null;
            }
        }
    }

    /**
     * Creates a pending payment.
     *
     * @return the payment details
     */
    public static PaymentDetails pending() {
        return new PaymentDetails(PaymentStatus.PENDING, null, null);
    }

    /**
     * Creates a paid payment.
     *
     * @param paidOn the payment date
     * @return the payment details
     */
    public static PaymentDetails paid(LocalDate paidOn) {
        return new PaymentDetails(PaymentStatus.PAID, paidOn, null);
    }

    /**
     * Creates an observed payment.
     *
     * @param reason the observation reason
     * @return the payment details
     */
    public static PaymentDetails observed(String reason) {
        return new PaymentDetails(PaymentStatus.OBSERVED, null, reason);
    }

    /**
     * Checks whether the payment is already done.
     *
     * @return true if PAID
     */
    public boolean isPaid() {
        return status == PaymentStatus.PAID;
    }
}
