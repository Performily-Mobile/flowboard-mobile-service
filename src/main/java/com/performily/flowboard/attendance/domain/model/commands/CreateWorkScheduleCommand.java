package com.performily.flowboard.attendance.domain.model.commands;
import java.time.*; import java.util.Set;

public record CreateWorkScheduleCommand(Long positionId, LocalTime startTime, LocalTime endTime, int toleranceMinutes, Set<DayOfWeek> days) 
{}
