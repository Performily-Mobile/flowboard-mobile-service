package com.performily.flowboard.attendance.domain.model.queries;
import java.time.LocalDate;

public record GetAttendanceRecordsByEmployeeIdQuery(Long employeeId, LocalDate fromDate, LocalDate toDate) 
{}
