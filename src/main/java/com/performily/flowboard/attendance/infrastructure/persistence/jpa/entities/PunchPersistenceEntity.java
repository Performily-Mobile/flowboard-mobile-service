package com.performily.flowboard.attendance.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.attendance.domain.model.valueobjects.PunchType; 
import jakarta.persistence.*; 
import java.time.LocalDateTime;

@Entity 
@Table(name="punches") 
public class PunchPersistenceEntity {
    
    public PunchPersistenceEntity() {}

    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private Long id; 
    
    @Column(name="employee_id", nullable=false) 
    private Long employeeId; 
    
    @Column(name="punched_at", nullable=false) 
    private LocalDateTime punchedAt; 
    
    @Enumerated(EnumType.STRING) 
    @Column(nullable=false) 
    private PunchType type; 
    
    @Column(name="attendance_record_id") 
    private Long attendanceRecordId;
    
    public Long getId() { return id; } 
    public void setId(Long v) { id = v; } 
    
    public Long getEmployeeId() { return employeeId; } 
    public void setEmployeeId(Long v) { employeeId = v; } 
    
    public LocalDateTime getPunchedAt() { return punchedAt; } 
    public void setPunchedAt(LocalDateTime v) { punchedAt = v; } 
    
    public PunchType getType() { return type; } 
    public void setType(PunchType v) { type = v; } 
    
    public Long getAttendanceRecordId() { return attendanceRecordId; } 
    public void setAttendanceRecordId(Long v) { attendanceRecordId = v; }
}