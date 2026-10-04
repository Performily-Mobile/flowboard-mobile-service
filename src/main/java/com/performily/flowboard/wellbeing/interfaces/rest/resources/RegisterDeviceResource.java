package com.performily.flowboard.wellbeing.interfaces.rest.resources;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record RegisterDeviceResource(@NotBlank String code, @NotEmpty Set<MetricType> supportedMetrics) {}
