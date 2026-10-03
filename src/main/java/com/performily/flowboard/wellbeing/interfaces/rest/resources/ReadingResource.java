package com.performily.flowboard.wellbeing.interfaces.rest.resources;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.HealthIndicator;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReadingResource(Long id, Long officeId, Long deviceId, MetricType metricType, BigDecimal value, String unit,
                              LocalDateTime recordedAt, HealthIndicator indicator) {}
