package com.performily.flowboard.attendance.domain.model.commands;

public record JustifyAttendanceCommand(
    Long attendanceRecordId, 
    String reason, 
    String evidenceUrl
) {}