package com.performily.flowboard.attendance.domain.model.aggregates;

import com.performily.flowboard.attendance.domain.model.entities.Punch;
import com.performily.flowboard.attendance.domain.model.entities.WorkSchedule;
import com.performily.flowboard.attendance.domain.model.events.*;
import com.performily.flowboard.attendance.domain.model.valueobjects.*;
import com.performily.flowboard.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.*;
import java.util.Comparator;
import java.util.List;

public class AttendanceRecord extends AbstractDomainAggregateRoot<AttendanceRecord> {
    private Long id; private final Long employeeId; private final LocalDate workDate;
    private LocalDateTime checkInTime; private LocalDateTime checkOutTime; private WorkedHours workedHours; private AttendanceStatus status; private String justificationReason;
    
    public AttendanceRecord(Long employeeId, LocalDate workDate, LocalDateTime checkInTime, LocalDateTime checkOutTime, WorkedHours workedHours, AttendanceStatus status, String justificationReason){
        this.employeeId=employeeId; this.workDate=workDate; this.checkInTime=checkInTime; this.checkOutTime=checkOutTime; this.workedHours=workedHours; this.status=status; this.justificationReason=justificationReason;
    }

    public static AttendanceRecord fromPunches(Long employeeId, LocalDate date, List<Punch> punches, WorkSchedule schedule){
        List<Punch> day=punches.stream().filter(p->p.occurredOn(date)).sorted(Comparator.comparing(Punch::getPunchedAt)).toList();
        LocalDateTime in=day.stream().filter(p->p.getType()==PunchType.CHECK_IN).map(Punch::getPunchedAt).findFirst().orElse(null);
        LocalDateTime out=day.stream().filter(p->p.getType()==PunchType.CHECK_OUT).map(Punch::getPunchedAt).reduce((a,b)->b).orElse(null);
        AttendanceStatus s;
        WorkedHours h=WorkedHours.empty();
        if(in==null) s=AttendanceStatus.ABSENT;
        else if(out==null) s=AttendanceStatus.INCOMPLETE;
        else {
            Duration actual=Duration.between(in.toLocalTime(),out.toLocalTime());
            Duration expected=Duration.ofMinutes(schedule.getTimeRange().durationInMinutes());
            Duration overtime=actual.compareTo(expected)>0?actual.minus(expected):Duration.ZERO;
            long delay=Math.max(0,Duration.between(schedule.getTimeRange().startTime(),in.toLocalTime()).toMinutes());
            s=schedule.getTolerance().allows(delay)?AttendanceStatus.ON_TIME:AttendanceStatus.LATE;
            h=new WorkedHours(actual,overtime);
        }
        AttendanceRecord r=new AttendanceRecord(employeeId,date,in,out,h,s,null);
        r.registerDomainEvent(new AttendanceRecordBuiltEvent(employeeId,date,s,LocalDateTime.now()));
        if(s==AttendanceStatus.LATE) r.registerDomainEvent(new LateArrivalDetectedEvent(employeeId,date,Math.max(0,Duration.between(schedule.getTimeRange().startTime(),in.toLocalTime()).toMinutes()),LocalDateTime.now()));
        if(s==AttendanceStatus.ABSENT) r.registerDomainEvent(new AbsenceDetectedEvent(employeeId,date,LocalDateTime.now()));
        if(!h.overtime().isZero()) r.registerDomainEvent(new OvertimeCalculatedEvent(employeeId,date,h.overtime().toMinutes(),LocalDateTime.now()));
        return r;
    }

    public static AttendanceRecord absence(Long employeeId, LocalDate date){
        AttendanceRecord r=new AttendanceRecord(employeeId,date,null,null,WorkedHours.empty(),AttendanceStatus.ABSENT,null);
        r.registerDomainEvent(new AttendanceRecordBuiltEvent(employeeId,date,AttendanceStatus.ABSENT,LocalDateTime.now()));
        r.registerDomainEvent(new AbsenceDetectedEvent(employeeId,date,LocalDateTime.now())); return r;
    }
    
    public void justify(String reason,String evidenceUrl){ if(reason==null||reason.isBlank()) throw new IllegalArgumentException("Justification reason is required"); this.justificationReason=reason; this.status=AttendanceStatus.JUSTIFIED; }
    
    public boolean isComplete(){return checkInTime!=null&&checkOutTime!=null;}
    
    public Long getId(){return id;} 
    
    public void setId(Long id){this.id=id;} 
    
    public Long getEmployeeId(){return employeeId;} 
    
    public LocalDate getWorkDate(){return workDate;}
    
    public LocalDateTime getCheckInTime(){return checkInTime;} 
    
    public LocalDateTime getCheckOutTime(){return checkOutTime;} 
    
    public WorkedHours getWorkedHours(){return workedHours;} 
    
    public AttendanceStatus getStatus(){return status;} 
    
    public String getJustificationReason(){return justificationReason;}
}
