package com.performily.flowboard.attendance.interfaces.rest.resources;

import com.performily.flowboard.attendance.domain.model.valueobjects.PunchType; 
import io.swagger.v3.oas.annotations.media.Schema; 
import jakarta.validation.constraints.NotNull; 
import java.time.LocalDateTime;

@Schema(name = "RegisterPunchRequest") 
public record RegisterPunchResource(
    @NotNull Long employeeId,
    @NotNull LocalDateTime punchedAt,
    @NotNull PunchType type
) {}