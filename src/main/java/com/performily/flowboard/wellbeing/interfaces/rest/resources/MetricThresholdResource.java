package com.performily.flowboard.wellbeing.interfaces.rest.resources;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;

import java.util.List;

public record MetricThresholdResource(Long id, Long officeId, MetricType metricType, String unit, List<ThresholdRangeResource> ranges) {}
