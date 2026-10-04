package com.performily.flowboard.wellbeing.interfaces.rest.resources;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.HealthIndicator;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * message is filled when there are no readings in the selected range.
 */
public record ReadingHistoryResource(Long officeId, MetricType metricType, String unit, LocalDate from, LocalDate to,
                                     BigDecimal minimum, BigDecimal maximum, BigDecimal average, int daysAboveAcceptable,
                                     List<DailyAverageResource> dailyAverages, List<ReadingResource> readings,
                                     String message) {
    public record DailyAverageResource(LocalDate date, BigDecimal average, HealthIndicator indicator) {}
}
