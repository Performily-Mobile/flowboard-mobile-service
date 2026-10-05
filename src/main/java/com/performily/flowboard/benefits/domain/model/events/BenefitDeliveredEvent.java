package com.performily.flowboard.benefits.domain.model.events;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain event registered when an assigned benefit is delivered.
 *
 * @param assignmentId the assignment
 * @param employeeId   the employee that received it
 * @param deliveredOn  the delivery date
 * @param occurredOn   when it was registered
 */
public record BenefitDeliveredEvent(Long assignmentId, Long employeeId, LocalDate deliveredOn, LocalDateTime occurredOn) {
}
