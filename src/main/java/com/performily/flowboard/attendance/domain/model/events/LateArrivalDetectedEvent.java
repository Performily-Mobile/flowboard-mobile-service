package com.performily.flowboard.attendance.domain.model.events;

import com.performily.flowboard.attendance.domain.model.valueobjects.AttendanceStatus;
import java.time.*;

public record LateArrivalDetectedEvent(
    Long employeeId, 
    LocalDate workDate, 
    long delayInMinutes, 
    LocalDateTime occurredOn
) {}