package com.performily.flowboard.benefits.domain.model.valueobjects;

/**
 * Assignment Status
 * @summary
 * Lifecycle of a benefit assignment: ASSIGNED -> DELIVERED | CANCELLED.
 * DELIVERED and CANCELLED are final.
 *
 * @since 1.0.0
 */
public enum AssignmentStatus {
    ASSIGNED,
    DELIVERED,
    CANCELLED
}
