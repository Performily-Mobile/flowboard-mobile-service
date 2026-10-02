package com.performily.flowboard.request.domain.model.queries;

/**
 * Get All Request Types Query
 * @summary
 * Query to get the request types. When activeOnly is true only the active types are returned (used by the app to build the form).
 *
 * @since 1.0.0
 */
public record GetAllRequestTypesQuery(boolean activeOnly) {
}
