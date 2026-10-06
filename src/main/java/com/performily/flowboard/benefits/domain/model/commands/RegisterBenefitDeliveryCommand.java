package com.performily.flowboard.benefits.domain.model.commands;

import java.time.LocalDate;

/**
 * Registers the effective delivery of an assigned benefit (US39).
 *
 * @param registeredById the HR employee that registers it, optional until IAM exists
 */
public record RegisterBenefitDeliveryCommand(Long assignmentId, LocalDate deliveredOn, Long registeredById, String notes) {
}
