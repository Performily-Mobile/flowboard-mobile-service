package com.performily.flowboard.attendance.interfaces.rest.resources;

import com.performily.flowboard.attendance.domain.model.valueobjects.AttendanceStatus; 
import io.swagger.v3.oas.annotations.media.Schema; 
import java.time.*;

@Schema(name = "AttendanceRecordResponse") 
public record AttendanceRecordResource(
    Long id,
    Long employeeId,
    LocalDate workDate,
    LocalDateTime checkInTime,
    LocalDateTime checkOutTime,
    double workedHours,
    double overtimeHours,
    AttendanceStatus status,
    String justificationReason
) {}