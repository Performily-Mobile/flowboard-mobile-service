package com.performily.flowboard.wellbeing.application.queryservices.views;

import com.performily.flowboard.wellbeing.domain.model.aggregates.Office;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.HealthIndicator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Environmental status of an office.
 *
 * @param overallIndicator the worst indicator among the up to date metrics, null when none is up to date
 * @param upToDate         true when at least one metric has a recent reading
 * @param lastReadingAt    the most recent reading of the office, null when it has none
 */
public record OfficeStatusView(
        Office office,
        boolean upToDate,
        HealthIndicator overallIndicator,
        LocalDateTime lastReadingAt,
        List<MetricStatusView> metrics,
        List<DeviceView> devices
) {}
