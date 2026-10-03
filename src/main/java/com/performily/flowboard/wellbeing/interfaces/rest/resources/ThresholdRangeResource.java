package com.performily.flowboard.wellbeing.interfaces.rest.resources;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.HealthIndicator;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ThresholdRangeResource(@NotNull HealthIndicator indicator, @NotNull BigDecimal minValue, @NotNull BigDecimal maxValue) {}
