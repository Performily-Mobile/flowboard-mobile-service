package com.performily.flowboard.attendance.application.internal.commandservices;

import com.performily.flowboard.attendance.application.commandservices.AttendanceCommandService;
import com.performily.flowboard.attendance.domain.model.aggregates.AttendanceRecord;
import com.performily.flowboard.attendance.domain.model.commands.*;
import com.performily.flowboard.attendance.domain.model.entities.Punch;
import com.performily.flowboard.attendance.domain.model.entities.WorkSchedule;
import com.performily.flowboard.attendance.domain.model.valueobjects.*;
import com.performily.flowboard.workspace.domain.model.valueobjects.EmploymentStatus;
import com.performily.flowboard.attendance.domain.repositories.*;
import com.performily.flowboard.shared.application.result.*;
import com.performily.flowboard.workspace.application.facades.WorkspaceContextFacade;
import org.springframework.stereotype.Service;

import java.time.*; 
import java.util.*;

@Service
public class AttendanceCommandServiceImpl implements AttendanceCommandService {
    
    private final AttendanceRecordRepository attendanceRecordRepository; 
    private final PunchRepository punchRepository; 
    private final WorkScheduleRepository workScheduleRepository; 
    private final WorkspaceContextFacade workspaceContextFacade;

    public AttendanceCommandServiceImpl(
            AttendanceRecordRepository a, 
            PunchRepository p, 
            WorkScheduleRepository w, 
            WorkspaceContextFacade f) {
        this.attendanceRecordRepository = a;
        this.punchRepository = p;
        this.workScheduleRepository = w;
        this.workspaceContextFacade = f;
    }

    public Result<Long, ApplicationError> handle(RegisterPunchCommand c) {
        if (c.employeeId() == null || c.punchedAt() == null || c.type() == null) {
            return Result.failure(ApplicationError.validationError("punch", "employeeId, punchedAt and type are required"));
        }
        
        var employee = workspaceContextFacade.findEmployeeById(c.employeeId()); 
        if (employee.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Employee", c.employeeId().toString()));
        }
        
        if (employee.get().getStatus() != EmploymentStatus.ACTIVE) {
            return Result.failure(ApplicationError.businessRuleViolation("attendance-punch", "Only ACTIVE employees can register attendance"));
        }
        
        try {
            var punch = punchRepository.save(new Punch(c.employeeId(), c.punchedAt(), c.type())); 
            var schedule = employee.get().getPosition() == null 
                ? Optional.<WorkSchedule>empty() 
                : workScheduleRepository.findByPositionId(employee.get().getPosition().getId());
            
            if (schedule.isPresent() && schedule.get().appliesOn(c.punchedAt().getDayOfWeek())) {
                buildAndSave(c.employeeId(), c.punchedAt().toLocalDate(), schedule.get());
            }
            
            return Result.success(punch.getId());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("register-punch", e.getMessage()));
        }
    }

    public Result<AttendanceRecord, ApplicationError> handle(BuildDailyAttendanceCommand c) {
        var employee = workspaceContextFacade.findEmployeeById(c.employeeId()); 
        if (employee.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Employee", c.employeeId().toString()));
        }
        
        if (c.workDate() == null) {
            return Result.failure(ApplicationError.validationError("workDate", "Work date is required"));
        }
        
        if (attendanceRecordRepository.existsByEmployeeIdAndWorkDate(c.employeeId(), c.workDate())) {
            return attendanceRecordRepository.findByEmployeeIdAndWorkDate(c.employeeId(), c.workDate())
                .map(Result::<AttendanceRecord, ApplicationError>success)
                .orElseThrow();
        }
        
        var position = employee.get().getPosition(); 
        if (position == null) {
            return Result.failure(ApplicationError.businessRuleViolation("attendance-schedule", "Employee has no position"));
        }
        
        var schedule = workScheduleRepository.findByPositionId(position.getId()); 
        if (schedule.isEmpty()) {
            return Result.failure(ApplicationError.businessRuleViolation("attendance-schedule", "Position has no work schedule"));
        }
        
        if (!schedule.get().appliesOn(c.workDate().getDayOfWeek())) {
            return Result.failure(ApplicationError.businessRuleViolation("attendance-schedule", "The selected date is not a scheduled work day"));
        }
        
        return Result.success(buildAndSave(c.employeeId(), c.workDate(), schedule.get()));
    }

    private AttendanceRecord buildAndSave(Long employeeId, LocalDate date, WorkSchedule schedule) {
        var punches = punchRepository.findAllByEmployeeIdAndDate(employeeId, date);
        var record = AttendanceRecord.fromPunches(employeeId, date, punches, schedule);
        return attendanceRecordRepository.save(record);
    }

    public Result<AttendanceRecord, ApplicationError> handle(JustifyAttendanceCommand c) {
        var record = attendanceRecordRepository.findById(c.attendanceRecordId());
        if (record.isEmpty()) {
            return Result.failure(ApplicationError.notFound("AttendanceRecord", c.attendanceRecordId().toString()));
        }
        
        try {
            record.get().justify(c.reason(), c.evidenceUrl());
            return Result.success(attendanceRecordRepository.save(record.get()));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("justification", e.getMessage()));
        }
    }

    public Result<WorkSchedule, ApplicationError> handle(CreateWorkScheduleCommand c) {
        try {
            if (c.positionId() == null) {
                return Result.failure(ApplicationError.validationError("positionId", "Position is required"));
            }
            
            if (workScheduleRepository.findByPositionId(c.positionId()).isPresent()) {
                return Result.failure(ApplicationError.conflict("WorkSchedule", "The position already has a work schedule"));
            }
            
            var s = new WorkSchedule(
                c.positionId(), 
                new TimeRange(c.startTime(), c.endTime()), 
                new LateTolerance(c.toleranceMinutes()), 
                c.days()
            );
            return Result.success(workScheduleRepository.save(s));
            
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("workSchedule", e.getMessage()));
        }
    }

    public Result<WorkSchedule, ApplicationError> handle(AssignWorkScheduleToPositionCommand c) {
        return workScheduleRepository.findById(c.workScheduleId())
            .map(s -> Result.<WorkSchedule, ApplicationError>success(s))
            .orElseGet(() -> Result.failure(ApplicationError.notFound("WorkSchedule", c.workScheduleId().toString())));
    }
}