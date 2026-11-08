package com.performily.flowboard.attendance.interfaces.rest;

import com.performily.flowboard.attendance.application.commandservices.AttendanceCommandService; 
import com.performily.flowboard.attendance.application.queryservices.AttendanceQueryService; 
import com.performily.flowboard.attendance.domain.model.commands.CreateWorkScheduleCommand; 
import com.performily.flowboard.attendance.domain.model.commands.AssignWorkScheduleToPositionCommand; 
import com.performily.flowboard.attendance.interfaces.rest.resources.*; 
import com.performily.flowboard.attendance.interfaces.rest.transform.WorkScheduleResourceFromEntityAssembler; 
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler; 
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag; 
import org.springframework.http.*; 
import org.springframework.web.bind.annotation.*; 

import java.util.List;

@RestController 
@RequestMapping("/api/v1/work-schedules")
@Tag(name = "Work Schedules", description = "Work schedule management") 
public class WorkSchedulesController { 
    
    private final AttendanceCommandService commandService; 
    private final AttendanceQueryService queryService; 

    public WorkSchedulesController(AttendanceCommandService c, AttendanceQueryService q) {
        commandService = c;
        queryService = q;
    }
    
    @Operation(summary = "Create work schedule")
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
    
    @Operation(summary = "Get all work schedules")
    @GetMapping 
    public ResponseEntity<List<WorkScheduleResource>> getAll() {
        return ResponseEntity.ok(
            queryService.getAllWorkSchedules().stream()
                .map(WorkScheduleResourceFromEntityAssembler::toResourceFromEntity)
                .toList()
        );
    }
    
    @Operation(summary = "Assign work schedule to position")
    @PutMapping("/positions/{positionId}/work-schedule/{workScheduleId}") 
    public ResponseEntity<?> assign(@PathVariable Long positionId, @PathVariable Long workScheduleId) {
        var result = commandService.handle(new AssignWorkScheduleToPositionCommand(positionId, workScheduleId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
            result, 
            WorkScheduleResourceFromEntityAssembler::toResourceFromEntity, 
            HttpStatus.OK
        );
    }
    
    @Operation(summary = "Get work schedule by ID")
    @GetMapping("/{id}") 
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return queryService.getWorkScheduleById(id)
            .map(s -> ResponseEntity.ok(WorkScheduleResourceFromEntityAssembler.toResourceFromEntity(s)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}