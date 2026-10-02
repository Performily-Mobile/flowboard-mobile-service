package com.performily.flowboard.attendance.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.attendance.domain.model.valueobjects.AttendanceStatus;
import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name="attendance_records", uniqueConstraints=@UniqueConstraint(name="uk_attendance_employee_date", columnNames={"employee_id","work_date"}))
public class AttendanceRecordPersistenceEntity {
    
    public AttendanceRecordPersistenceEntity() {}

    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private Long id;
    
    @Column(name="employee_id", nullable=false) 
    private Long employeeId;
    
    @Column(name="work_date", nullable=false) 
    private LocalDate workDate;
    
    @Column(name="check_in_time") 
    private LocalDateTime checkInTime;
    
    @Column(name="check_out_time") 
    private LocalDateTime checkOutTime;
    
    @Column(name="worked_minutes", nullable=false) 
    private long workedMinutes;
    
    @Column(name="overtime_minutes", nullable=false) 
    private long overtimeMinutes;
    
    @Enumerated(EnumType.STRING) 
    @Column(nullable=false) 
    private AttendanceStatus status;
    
    @Column(length=500) 
    private String justificationReason;
    
    public Long getId() { return id; } 
    public void setId(Long id) { this.id = id; } 
    
    public Long getEmployeeId() { return employeeId; } 
    public void setEmployeeId(Long v) { employeeId = v; } 
    
    public LocalDate getWorkDate() { return workDate; } 
    public void setWorkDate(LocalDate v) { workDate = v; } 
    
    public LocalDateTime getCheckInTime() { return checkInTime; } 
    public void setCheckInTime(LocalDateTime v) { checkInTime = v; } 
    
    public LocalDateTime getCheckOutTime() { return checkOutTime; } 
    public void setCheckOutTime(LocalDateTime v) { checkOutTime = v; } 
    
    public long getWorkedMinutes() { return workedMinutes; } 
    public void setWorkedMinutes(long v) { workedMinutes = v; } 
    
    public long getOvertimeMinutes() { return overtimeMinutes; } 
    public void setOvertimeMinutes(long v) { overtimeMinutes = v; } 
    
    public AttendanceStatus getStatus() { return status; } 
    public void setStatus(AttendanceStatus v) { status = v; } 
    
    public String getJustificationReason() { return justificationReason; } 
    public void setJustificationReason(String v) { justificationReason = v; }
}