package com.performily.flowboard.benefits.domain.model.events;

import java.time.LocalDateTime;

/**
 * Domain event registered when a benefit is assigned to an employee
 * ("Beneficio Asignado" in the Bounded Context Canvas).
 *
 * @param assignmentId    the assignment
 * @param employeeId      the employee that receives it
 * @param benefitTypeName the name of the benefit type
 * @param occurredOn      when it happened
 */
public record BenefitAssignedEvent(Long assignmentId, Long employeeId, String benefitTypeName, LocalDateTime occurredOn) {
}
