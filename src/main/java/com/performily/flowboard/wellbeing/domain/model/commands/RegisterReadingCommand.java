package com.performily.flowboard.wellbeing.domain.model.commands;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @param recordedAt optional; when null the current time is used
 */
public record RegisterReadingCommand(String deviceCode, MetricType metricType, BigDecimal value, LocalDateTime recordedAt) {}
