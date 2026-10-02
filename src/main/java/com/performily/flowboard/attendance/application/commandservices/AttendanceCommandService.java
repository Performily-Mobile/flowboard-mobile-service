package com.performily.flowboard.attendance.application.commandservices;

import com.performily.flowboard.attendance.domain.model.aggregates.AttendanceRecord; 
import com.performily.flowboard.attendance.domain.model.commands.*; 
import com.performily.flowboard.attendance.domain.model.entities.WorkSchedule; 
import com.performily.flowboard.shared.application.result.*;

public interface AttendanceCommandService { 
    Result<Long, ApplicationError> handle(RegisterPunchCommand command); 
    Result<AttendanceRecord, ApplicationError> handle(BuildDailyAttendanceCommand command); 
    Result<AttendanceRecord, ApplicationError> handle(JustifyAttendanceCommand command); 
    Result<WorkSchedule, ApplicationError> handle(CreateWorkScheduleCommand command); 
    Result<WorkSchedule, ApplicationError> handle(AssignWorkScheduleToPositionCommand command); 
}