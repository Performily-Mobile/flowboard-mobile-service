package com.performily.flowboard.request.domain.model.valueobjects;

/**
 * Request Status
 * @summary
 * Lifecycle states of a request.
 *
 * Transitions:
 * IN_PROGRESS -> APPROVED | REJECTED | UNDER_REVIEW | CANCELLED
 * UNDER_REVIEW -> IN_PROGRESS (resubmit) | CANCELLED
 * APPROVED, REJECTED and CANCELLED are final.
 *
 * @since 1.0.0
 */
public enum RequestStatus {
    IN_PROGRESS,
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
    CANCELLED;

    /**
     * Checks whether the status is final.
     *
     * @return true for APPROVED, REJECTED and CANCELLED
     */
    public boolean isFinal() {
        return this == APPROVED || this == REJECTED || this == CANCELLED;
    }
}
