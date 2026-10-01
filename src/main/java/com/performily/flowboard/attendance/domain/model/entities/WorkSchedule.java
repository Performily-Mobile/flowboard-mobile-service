package com.performily.flowboard.attendance.domain.model.entities;

import com.performily.flowboard.attendance.domain.model.valueobjects.LateTolerance;
import com.performily.flowboard.attendance.domain.model.valueobjects.TimeRange;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

public class WorkSchedule {
    private Long id;
    private Long positionId;
    private TimeRange timeRange;
    private LateTolerance tolerance;
    private final Set<DayOfWeek> days;

    public WorkSchedule(Long positionId, TimeRange timeRange, LateTolerance tolerance, Set<DayOfWeek> days) {
        if (positionId == null || timeRange == null || tolerance == null) throw new IllegalArgumentException("Work schedule data is required");
        if (days == null || days.isEmpty()) throw new IllegalArgumentException("At least one work day is required");
        this.positionId=positionId; this.timeRange=timeRange; this.tolerance=tolerance; this.days=new HashSet<>(days);
    }
    public WorkSchedule(Long id, Long positionId, TimeRange timeRange, LateTolerance tolerance, Set<DayOfWeek> days) { this(positionId,timeRange,tolerance,days); this.id=id; }
    
    public boolean appliesOn(DayOfWeek day){return days.contains(day);}
    
    public void assignToPosition(Long positionId){ if(positionId==null) throw new IllegalArgumentException("Position id cannot be null"); this.positionId=positionId; }
    
    public Long getId(){return id;} 
    
    public void setId(Long id){this.id=id;} 
    
    public Long getPositionId(){return positionId;}
    
    public TimeRange getTimeRange(){return timeRange;} 
    
    public LateTolerance getTolerance(){return tolerance;} 
    
    public Set<DayOfWeek> getDays(){return Set.copyOf(days);}
    
    public void update(TimeRange timeRange, LateTolerance tolerance, Set<DayOfWeek> days){
        if(days==null||days.isEmpty()) throw new IllegalArgumentException("At least one work day is required");
        this.timeRange=timeRange; this.tolerance=tolerance; this.days.clear(); this.days.addAll(days);
    }
}
