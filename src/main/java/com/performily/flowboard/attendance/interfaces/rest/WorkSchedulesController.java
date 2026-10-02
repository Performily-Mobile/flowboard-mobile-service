package com.performily.flowboard.attendance.interfaces.rest;

import com.performily.flowboard.attendance.application.commandservices.AttendanceCommandService; 
import com.performily.flowboard.attendance.application.queryservices.AttendanceQueryService; 
import com.performily.flowboard.attendance.domain.model.commands.CreateWorkScheduleCommand; 
import com.performily.flowboard.attendance.domain.model.commands.AssignWorkScheduleToPositionCommand; 
import com.performily.flowboard.attendance.interfaces.rest.resources.*; 
import com.performily.flowboard.attendance.interfaces.rest.transform.WorkScheduleResourceFromEntityAssembler; 
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler; 
import jakarta.validation.Valid; 
import org.springframework.http.*; 
import org.springframework.web.bind.annotation.*; 

import java.util.List;

@RestController 
@RequestMapping("/api/v1/work-schedules") 
public class WorkSchedulesController { 
    
    private final AttendanceCommandService commandService; 
    private final AttendanceQueryService queryService; 

    public WorkSchedulesController(AttendanceCommandService c, AttendanceQueryService q) {
        commandService = c;
        queryService = q;
    }
    
    @PostMapping 
    public ResponseEntity<?> create(@Valid @RequestBody CreateWorkScheduleResource r) {
        var result = commandService.handle(new CreateWorkScheduleCommand(
            r.positionId(), 
            r.startTime(), 
            r.endTime(), 
            r.toleranceMinutes(), 
            r.days()
        ));
        return ResponseEntityAssembler.toResponseEntityFromResult(
            result, 
            WorkScheduleResourceFromEntityAssembler::toResourceFromEntity, 
            HttpStatus.CREATED
        );
    }
    
    @GetMapping 
    public ResponseEntity<List<WorkScheduleResource>> getAll() {
        return ResponseEntity.ok(
            queryService.getAllWorkSchedules().stream()
                .map(WorkScheduleResourceFromEntityAssembler::toResourceFromEntity)
                .toList()
        );
    }
    
    @PutMapping("/positions/{positionId}/work-schedule/{workScheduleId}") 
    public ResponseEntity<?> assign(@PathVariable Long positionId, @PathVariable Long workScheduleId) {
        var result = commandService.handle(new AssignWorkScheduleToPositionCommand(positionId, workScheduleId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
            result, 
            WorkScheduleResourceFromEntityAssembler::toResourceFromEntity, 
            HttpStatus.OK
        );
    }
    
    @GetMapping("/{id}") 
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return queryService.getWorkScheduleById(id)
            .map(s -> ResponseEntity.ok(WorkScheduleResourceFromEntityAssembler.toResourceFromEntity(s)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}