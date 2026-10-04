package com.performily.flowboard.wellbeing.interfaces.rest.resources;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * recordedAt is optional, the current time is used when it is missing.
 */
public record RegisterReadingResource(@NotBlank String deviceCode, @NotNull MetricType metricType,
                                      @NotNull BigDecimal value, LocalDateTime recordedAt) {}
