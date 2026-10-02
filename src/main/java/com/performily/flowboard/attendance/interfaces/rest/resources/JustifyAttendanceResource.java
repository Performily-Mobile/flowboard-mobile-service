package com.performily.flowboard.attendance.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record JustifyAttendanceResource(
    @NotBlank String reason,
    String evidenceUrl
) {}