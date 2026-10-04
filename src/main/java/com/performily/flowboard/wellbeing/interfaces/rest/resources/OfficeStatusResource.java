package com.performily.flowboard.wellbeing.interfaces.rest.resources;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.HealthIndicator;

import java.time.LocalDateTime;
import java.util.List;

public record OfficeStatusResource(Long id, String name, String area, String address, String floor, String reference,
                                   boolean active, boolean upToDate, HealthIndicator overallIndicator,
                                   LocalDateTime lastReadingAt, List<MetricStatusResource> metrics,
                                   List<DeviceResource> devices) {}
