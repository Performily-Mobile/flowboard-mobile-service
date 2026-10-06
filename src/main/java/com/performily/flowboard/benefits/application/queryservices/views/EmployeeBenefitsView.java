package com.performily.flowboard.benefits.application.queryservices.views;

import com.performily.flowboard.benefits.domain.model.aggregates.BenefitAssignment;

import java.util.List;

/**
 * Benefits of one employee split as the "Mis beneficios" screen shows them.
 *
 * @param current   assigned, not delivered and still valid ("Vigentes")
 * @param delivered already delivered, newest first ("Entregados")
 */
public record EmployeeBenefitsView(Long employeeId, List<BenefitAssignment> current, List<BenefitAssignment> delivered) {
}
