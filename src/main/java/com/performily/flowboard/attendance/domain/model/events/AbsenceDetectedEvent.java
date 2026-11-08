package com.performily.flowboard.attendance.domain.model.events;

import java.time.*;

public record AbsenceDetectedEvent(
    Long employeeId, 
    LocalDate workDate, 
    LocalDateTime occurredOn
) {}