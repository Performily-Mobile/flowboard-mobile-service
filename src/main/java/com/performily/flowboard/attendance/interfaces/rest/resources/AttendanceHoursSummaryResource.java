package com.performily.flowboard.attendance.interfaces.rest.resources;

public record AttendanceHoursSummaryResource(
    Long employeeId,
    Double workedHours,
    Double overtimeHours
) {}