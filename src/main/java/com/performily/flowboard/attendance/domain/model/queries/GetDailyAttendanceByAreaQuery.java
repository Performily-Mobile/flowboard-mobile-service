package com.performily.flowboard.attendance.domain.model.queries;
import java.time.LocalDate;

public record GetDailyAttendanceByAreaQuery(Long areaId, LocalDate workDate) 
{}
