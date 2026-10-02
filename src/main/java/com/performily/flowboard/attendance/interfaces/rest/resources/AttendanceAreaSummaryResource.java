package com.performily.flowboard.attendance.interfaces.rest.resources;

public record AttendanceAreaSummaryResource(
    Long areaId,
    long onTime,
    long late,
    long absent,
    long incomplete,
    long justified,
    double workedHours,
    double overtimeHours
) {}