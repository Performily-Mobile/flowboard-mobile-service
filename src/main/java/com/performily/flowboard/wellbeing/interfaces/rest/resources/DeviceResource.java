package com.performily.flowboard.wellbeing.interfaces.rest.resources;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceStatus;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;

import java.time.LocalDateTime;
import java.util.Set;

public record DeviceResource(Long id, String code, Set<MetricType> supportedMetrics, DeviceStatus status,
                             Long officeId, LocalDateTime lastReadingAt) {}
