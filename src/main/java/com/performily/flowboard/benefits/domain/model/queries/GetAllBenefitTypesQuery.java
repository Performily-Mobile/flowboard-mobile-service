package com.performily.flowboard.benefits.domain.model.queries;

/**
 * Benefit types of the catalog.
 *
 * @param activeOnly when true only active types are returned
 */
public record GetAllBenefitTypesQuery(boolean activeOnly) {
}
