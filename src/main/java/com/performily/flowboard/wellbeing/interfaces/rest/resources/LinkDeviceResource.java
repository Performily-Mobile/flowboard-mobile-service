package com.performily.flowboard.wellbeing.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record LinkDeviceResource(@NotBlank String deviceCode) {}
