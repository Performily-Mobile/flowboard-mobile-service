package com.performily.flowboard.wellbeing.domain.model.commands;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.ThresholdRange;
import java.util.List;

public record DefineMetricThresholdCommand(Long officeId, MetricType metricType, List<ThresholdRange> ranges) {}
