package com.performily.flowboard.attendance.domain.model.events;

import java.time.*;

public record LateArrivalDetectedEvent(
    Long employeeId, 
    LocalDate workDate, 
    long delayInMinutes, 
    LocalDateTime occurredOn
) {}