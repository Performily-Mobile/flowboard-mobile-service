package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Organization Chart Node Resource
 * @summary
 * Resource for one node of the organization chart.
 *
 * @since 1.0.0
 */
@Schema(name = "OrganizationChartNodeResponse", description = "Organization chart node with its direct subordinates")
public record OrganizationChartNodeResource(
        @Schema(description = "Employee identifier", example = "1") Long employeeId,
        @Schema(description = "Full name", example = "María Quispe Rojas") String fullName,
        @Schema(description = "Position title", example = "Jefa de RR.HH.") String positionTitle,
        @Schema(description = "Area name", example = "Recursos Humanos") String areaName,
        @Schema(description = "Direct subordinates") List<OrganizationChartNodeResource> subordinates
) {
}