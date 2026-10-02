package com.performily.flowboard.attendance.interfaces.rest.resources;

import java.time.*; 
import java.util.Set;

public record WorkScheduleResource(
    Long id,
    Long positionId,
    LocalTime startTime,
    LocalTime endTime,
    int toleranceMinutes,
    Set<DayOfWeek> days
) {}