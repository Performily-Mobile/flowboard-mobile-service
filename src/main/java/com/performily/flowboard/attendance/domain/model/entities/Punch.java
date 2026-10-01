package com.performily.flowboard.attendance.domain.model.entities;

import com.performily.flowboard.attendance.domain.model.valueobjects.PunchType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Punch {
    private Long id;
    private final Long employeeId;
    private final LocalDateTime punchedAt;
    private final PunchType type;
    private Long attendanceRecordId;

    public Punch(Long employeeId, LocalDateTime punchedAt, PunchType type) {
        this.employeeId = Objects.requireNonNull(employeeId);
        this.punchedAt = Objects.requireNonNull(punchedAt);
        this.type = Objects.requireNonNull(type);
    }

    public Punch(Long id, Long employeeId, LocalDateTime punchedAt, PunchType type, Long attendanceRecordId) {
        this.id=id; this.employeeId=employeeId; this.punchedAt=punchedAt; this.type=type; this.attendanceRecordId=attendanceRecordId;
    }

    public boolean occurredOn(LocalDate date) { return punchedAt.toLocalDate().equals(date); }
    
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    
    public Long getEmployeeId(){return employeeId;} 
    
    public LocalDateTime getPunchedAt(){return punchedAt;} 
    
    public PunchType getType(){return type;}
    
    public Long getAttendanceRecordId(){return attendanceRecordId;} 
    
    public void setAttendanceRecordId(Long id){this.attendanceRecordId=id;}
}
