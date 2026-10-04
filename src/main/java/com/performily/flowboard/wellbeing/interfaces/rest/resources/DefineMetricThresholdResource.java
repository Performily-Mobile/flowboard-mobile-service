package com.performily.flowboard.wellbeing.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DefineMetricThresholdResource(@NotNull @Size(min = 2, max = 4) List<@Valid ThresholdRangeResource> ranges) {}
