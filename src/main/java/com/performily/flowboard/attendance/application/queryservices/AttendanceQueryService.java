package com.performily.flowboard.attendance.application.queryservices;

import com.performily.flowboard.attendance.domain.model.aggregates.AttendanceRecord; 
import com.performily.flowboard.attendance.domain.model.entities.WorkSchedule; 
import com.performily.flowboard.attendance.domain.model.queries.*; 
import java.util.*;

public interface AttendanceQueryService { 
    List<AttendanceRecord> handle(GetAttendanceRecordsByEmployeeIdQuery query); 
    List<AttendanceRecord> handle(GetDailyAttendanceByAreaQuery query); 
    List<WorkSchedule> getAllWorkSchedules(); 
    Optional<WorkSchedule> getWorkScheduleById(Long id);
    
    com.performily.flowboard.attendance.interfaces.rest.resources.AttendanceAreaSummaryResource getAreaSummary(
        Long areaId, 
        java.time.LocalDate from, 
        java.time.LocalDate to
    ); 
}