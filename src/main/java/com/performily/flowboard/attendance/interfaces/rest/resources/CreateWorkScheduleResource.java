package com.performily.flowboard.attendance.interfaces.rest.resources;

import jakarta.validation.constraints.*; 
import java.time.*; 
import java.util.Set;

public record CreateWorkScheduleResource(
    @NotNull Long positionId,
    @NotNull LocalTime startTime,
    @NotNull LocalTime endTime,
    @Min(0) int toleranceMinutes,
    @NotEmpty Set<DayOfWeek> days
) {}