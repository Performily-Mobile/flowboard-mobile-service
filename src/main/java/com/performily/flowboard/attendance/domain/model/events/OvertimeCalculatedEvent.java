package com.performily.flowboard.attendance.domain.model.events;

import java.time.*;

public record OvertimeCalculatedEvent(
    Long employeeId, 
    LocalDate workDate, 
    long minutes, 
    LocalDateTime occurredOn
) {}