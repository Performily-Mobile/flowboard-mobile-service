package com.performily.flowboard.attendance.interfaces.rest.transform;

import com.performily.flowboard.attendance.domain.model.aggregates.AttendanceRecord; 
import com.performily.flowboard.attendance.interfaces.rest.resources.AttendanceRecordResource;

public class AttendanceRecordResourceFromEntityAssembler { 
    
    public static AttendanceRecordResource toResourceFromEntity(AttendanceRecord r) {
        return new AttendanceRecordResource(
            r.getId(),
            r.getEmployeeId(),
            r.getWorkDate(),
            r.getCheckInTime(),
            r.getCheckOutTime(),
            r.getWorkedHours().toDecimalHours(),
            r.getWorkedHours().overtimeDecimalHours(),
            r.getStatus(),
            r.getJustificationReason()
        );
    } 
}