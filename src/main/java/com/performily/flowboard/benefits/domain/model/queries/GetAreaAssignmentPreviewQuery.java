package com.performily.flowboard.benefits.domain.model.queries;

import java.time.LocalDate;

/**
 * Preview of an assignment to an area: how many active employees will receive
 * the benefit and how many will be skipped because they already have it.
 */
public record GetAreaAssignmentPreviewQuery(Long benefitTypeId, Long areaId, LocalDate startDate, LocalDate endDate) {
}
