package com.performily.flowboard.wellbeing.domain.model.commands;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import java.util.Set;

public record RegisterDeviceCommand(String code, Set<MetricType> supportedMetrics) {}
