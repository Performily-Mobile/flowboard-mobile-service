package com.performily.flowboard.wellbeing.interfaces.rest.resources;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.HealthIndicator;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * indicator is null when upToDate is false: the information is not up to date.
 */
public record MetricStatusResource(MetricType metricType, String unit, BigDecimal lastValue, LocalDateTime lastRecordedAt,
                                   boolean upToDate, HealthIndicator indicator,
                                   BigDecimal optimalMin, BigDecimal optimalMax) {}
