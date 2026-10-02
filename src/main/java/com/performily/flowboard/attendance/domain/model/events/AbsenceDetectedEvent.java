package com.performily.flowboard.attendance.domain.model.events;

import com.performily.flowboard.attendance.domain.model.valueobjects.AttendanceStatus;
import java.time.*;

public record AbsenceDetectedEvent(
    Long employeeId, 
    LocalDate workDate, 
    LocalDateTime occurredOn
) {}