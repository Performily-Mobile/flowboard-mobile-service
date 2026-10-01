package com.performily.flowboard.attendance.domain.model.events;
import com.performily.flowboard.attendance.domain.model.valueobjects.AttendanceStatus;
import java.time.*;

public record AttendanceRecordBuiltEvent(Long employeeId, LocalDate workDate, AttendanceStatus status, LocalDateTime occurredOn) 
{}
