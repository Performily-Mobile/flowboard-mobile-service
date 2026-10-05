package com.performily.flowboard.benefits.application.queryservices.views;

/**
 * What would happen if a benefit is assigned to an area, before confirming it:
 * "Se asignará a los 48 colaboradores activos del área. 2 ya tienen este beneficio
 * en el periodo y serán omitidos. Asignar a 46".
 */
public record AreaAssignmentPreview(Long areaId, int activeEmployees, int alreadyAssigned, int toAssign) {
}
