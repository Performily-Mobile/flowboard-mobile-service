package com.performily.flowboard.workspace.domain.model.queries;

/**
 * Get Organization Chart Query
 * @summary
 * Query to get the organization chart, built from the directManagerId of the employees that are not TERMINATED.
 *
 * @since 1.0.0
 */
public record GetOrganizationChartQuery() {
}