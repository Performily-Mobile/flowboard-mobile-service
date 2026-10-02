package com.performily.flowboard.attendance.domain.model.commands;

import com.performily.flowboard.attendance.domain.model.valueobjects.PunchType;
import java.time.LocalDateTime;

public record RegisterPunchCommand(
    Long employeeId, 
    LocalDateTime punchedAt, 
    PunchType type
) {}