package com.performily.flowboard.attendance.application.internal.queryservices;

import com.performily.flowboard.attendance.application.queryservices.AttendanceQueryService; 
import com.performily.flowboard.attendance.domain.model.aggregates.AttendanceRecord; 
import com.performily.flowboard.attendance.domain.model.entities.WorkSchedule; 
import com.performily.flowboard.attendance.domain.model.queries.*; 
import com.performily.flowboard.attendance.domain.repositories.*; 
import com.performily.flowboard.workspace.application.facades.WorkspaceContextFacade; 
import org.springframework.stereotype.Service; 

import java.util.*;

@Service 
public class AttendanceQueryServiceImpl implements AttendanceQueryService { 
    
    private final AttendanceRecordRepository records; 
    private final WorkScheduleRepository schedules; 
    private final WorkspaceContextFacade workspace;

    public AttendanceQueryServiceImpl(AttendanceRecordRepository r, WorkScheduleRepository s, WorkspaceContextFacade w) {
        this.records = r;
        this.schedules = s;
        this.workspace = w;
    }

    public List<AttendanceRecord> handle(GetAttendanceRecordsByEmployeeIdQuery q) {
        return records.findAllByEmployeeIdAndWorkDateBetween(q.employeeId(), q.fromDate(), q.toDate());
    }

    public List<AttendanceRecord> handle(GetDailyAttendanceByAreaQuery q) {
        return workspace.findActiveEmployeesByAreaId(q.areaId()).stream()
            .map(e -> records.findByEmployeeIdAndWorkDate(e.getId(), q.workDate()).orElse(null))
            .filter(Objects::nonNull)
            .toList();
    }

    public List<WorkSchedule> getAllWorkSchedules() {
        return schedules.findAll();
    } 
    
    public Optional<WorkSchedule> getWorkScheduleById(Long id) {
        return schedules.findById(id);
    }

    public com.performily.flowboard.attendance.interfaces.rest.resources.AttendanceAreaSummaryResource getAreaSummary(
            Long areaId, 
            java.time.LocalDate from, 
            java.time.LocalDate to) {
        
        long on = 0, late = 0, abs = 0, inc = 0, just = 0; 
        double worked = 0, ot = 0; 
        
        for (var e : workspace.findActiveEmployeesByAreaId(areaId)) {
            for (var r : records.findAllByEmployeeIdAndWorkDateBetween(e.getId(), from, to)) {
                switch (r.getStatus()) {
                    case ON_TIME -> on++;
                    case LATE -> late++;
                    case ABSENT -> abs++;
                    case INCOMPLETE -> inc++;
                    case JUSTIFIED -> just++;
                } 
                worked += r.getWorkedHours().toDecimalHours(); 
                ot += r.getWorkedHours().overtimeDecimalHours();
            }
        } 
        
        return new com.performily.flowboard.attendance.interfaces.rest.resources.AttendanceAreaSummaryResource(
            areaId, on, late, abs, inc, just, worked, ot
        );
    }
}