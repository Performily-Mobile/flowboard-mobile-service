package com.performily.flowboard.wellbeing.application.queryservices.views;

import com.performily.flowboard.wellbeing.domain.model.entities.EnvironmentalReading;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.HealthIndicator;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Chronological series of readings of one metric in an office, with its summary.
 * minimum, maximum and average are null when there are no readings.
 *
 * @param daysAboveAcceptable days whose average is POOR or HAZARDOUS
 */
public record ReadingHistoryView(
        Long officeId,
        MetricType metricType,
        LocalDate from,
        LocalDate to,
        List<EnvironmentalReading> readings,
        List<DailyAverage> dailyAverages,
        BigDecimal minimum,
        BigDecimal maximum,
        BigDecimal average,
        int daysAboveAcceptable
) {
    public record DailyAverage(LocalDate date, BigDecimal average, HealthIndicator indicator) {}

    public boolean isEmpty() {
        return readings.isEmpty();
    }
}
