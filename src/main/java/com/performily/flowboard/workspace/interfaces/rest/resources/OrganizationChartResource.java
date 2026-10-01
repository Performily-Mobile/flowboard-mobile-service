package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Organization Chart Resource
 * @summary
 * Resource for the organization chart: the hierarchy tree and, apart, the
 * employees whose direct manager is no longer active (pending reassignment).
 *
 * @since 1.0.0
 */
@Schema(name = "OrganizationChartResponse", description = "Organization chart with its pending reassignments")
public record OrganizationChartResource(
        @Schema(description = "Root nodes of the hierarchy, each one with its subordinates")
        List<OrganizationChartNodeResource> nodes,

        @Schema(description = "Employees whose direct manager is no longer active")
        List<OrganizationChartNodeResource> pendingReassignment
) {
}