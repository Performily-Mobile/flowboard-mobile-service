package com.performily.flowboard.attendance.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*; 
import java.time.DayOfWeek;
import java.time.LocalTime; 
import java.util.*;

@Entity 
@Table(name="work_schedules") 
public class WorkSchedulePersistenceEntity {
    
    public WorkSchedulePersistenceEntity() {}

    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private Long id; 
    
    @Column(name="position_id", nullable=false, unique=true) 
    private Long positionId; 
    
    @Column(name="start_time", nullable=false) 
    private LocalTime startTime; 
    
    @Column(name="end_time", nullable=false) 
    private LocalTime endTime; 
    
    @Column(name="tolerance_minutes", nullable=false) 
    private int toleranceMinutes;
    
    @ElementCollection(fetch=FetchType.EAGER) 
    @CollectionTable(name="work_schedule_days", joinColumns=@JoinColumn(name="work_schedule_id")) 
    @Column(name="day_of_week", nullable=false) 
    @Enumerated(EnumType.STRING) 
    private Set<DayOfWeek> days = new HashSet<>();
    
    public Long getId() { return id; } 
    public void setId(Long v) { id = v; } 
    
    public Long getPositionId() { return positionId; } 
    public void setPositionId(Long v) { positionId = v; } 
    
    public LocalTime getStartTime() { return startTime; } 
    public void setStartTime(LocalTime v) { startTime = v; } 
    
    public LocalTime getEndTime() { return endTime; } 
    public void setEndTime(LocalTime v) { endTime = v; } 
    
    public int getToleranceMinutes() { return toleranceMinutes; } 
    public void setToleranceMinutes(int v) { toleranceMinutes = v; } 
    
    public Set<DayOfWeek> getDays() { return days; } 
    public void setDays(Set<DayOfWeek> v) { days = v; }
}