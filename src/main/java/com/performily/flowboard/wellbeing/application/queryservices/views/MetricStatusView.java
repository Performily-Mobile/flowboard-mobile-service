package com.performily.flowboard.wellbeing.application.queryservices.views;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.HealthIndicator;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.ThresholdRange;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Current condition of one metric in an office.
 * When the last reading is older than the validity window, upToDate is false
 * and indicator is null (no health indicator is emitted).
 *
 * @param optimalRange the OPTIMAL range of the office threshold, if defined
 */
public record MetricStatusView(
        MetricType metricType,
        BigDecimal lastValue,
        LocalDateTime lastRecordedAt,
        boolean upToDate,
        HealthIndicator indicator,
        ThresholdRange optimalRange
) {}
