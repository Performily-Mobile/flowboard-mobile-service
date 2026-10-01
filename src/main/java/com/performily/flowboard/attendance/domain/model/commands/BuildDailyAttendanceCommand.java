package com.performily.flowboard.attendance.domain.model.commands;
import java.time.LocalDate;

public record BuildDailyAttendanceCommand(Long employeeId, LocalDate workDate) 
{}
