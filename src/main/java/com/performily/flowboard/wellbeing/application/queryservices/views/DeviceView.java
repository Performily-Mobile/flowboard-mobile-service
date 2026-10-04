package com.performily.flowboard.wellbeing.application.queryservices.views;

import com.performily.flowboard.wellbeing.domain.model.entities.Device;
import java.time.LocalDateTime;

public record DeviceView(Device device, LocalDateTime lastReadingAt) {}
