package com.performily.flowboard.wellbeing.domain.model.commands;

public record UnlinkDeviceCommand(Long officeId, String deviceCode) {}
