package com.performily.flowboard.attendance.interfaces.rest;

import com.performily.flowboard.attendance.application.commandservices.AttendanceCommandService; 
import com.performily.flowboard.attendance.application.queryservices.AttendanceQueryService; 
import com.performily.flowboard.attendance.domain.model.commands.*; 
import com.performily.flowboard.attendance.domain.model.queries.*; 
import com.performily.flowboard.attendance.interfaces.rest.resources.*; 
import com.performily.flowboard.attendance.interfaces.rest.transform.*; 
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler; 
import io.swagger.v3.oas.annotations.Operation; 
import io.swagger.v3.oas.annotations.tags.Tag; 
import jakarta.validation.Valid; 
import org.springframework.http.*; 
import org.springframework.web.bind.annotation.*; 

import java.time.LocalDate; 
import java.util.List;

@RestController 
@RequestMapping("/api/v1/attendance") 
@Tag(name="Attendance") 
public class AttendanceController {
    
    private final AttendanceCommandService commandService; 
    private final AttendanceQueryService queryService; 

    public AttendanceController(AttendanceCommandService c, AttendanceQueryService q) {
        commandService = c;
        queryService = q;
    }
    
    @PostMapping("/punches") 
    @Operation(summary="Register attendance punch") 
    public ResponseEntity<?> registerPunch(@Valid @RequestBody RegisterPunchResource resource) {
        var result = commandService.handle(RegisterPunchCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, id -> id, HttpStatus.CREATED);
    }
    
    @GetMapping("/employees/{employeeId}") 
    @Operation(summary="Get attendance history by employee") 
    public ResponseEntity<List<AttendanceRecordResource>> getByEmployee(
            @PathVariable Long employeeId, 
            @RequestParam LocalDate from, 
            @RequestParam LocalDate to) {
        return ResponseEntity.ok(
            queryService.handle(new GetAttendanceRecordsByEmployeeIdQuery(employeeId, from, to)).stream()
                .map(AttendanceRecordResourceFromEntityAssembler::toResourceFromEntity)
                .toList()
        );
    }
    
    @GetMapping("/me") 
    public ResponseEntity<List<AttendanceRecordResource>> getMyAttendance(
            @RequestParam Long employeeId, 
            @RequestParam LocalDate from, 
            @RequestParam LocalDate to) {
        return getByEmployee(employeeId, from, to);
    }
    
    @GetMapping("/areas/{areaId}") 
    public ResponseEntity<List<AttendanceRecordResource>> getByArea(
            @PathVariable Long areaId, 
            @RequestParam LocalDate workDate) {
        return ResponseEntity.ok(
            queryService.handle(new GetDailyAttendanceByAreaQuery(areaId, workDate)).stream()
                .map(AttendanceRecordResourceFromEntityAssembler::toResourceFromEntity)
                .toList()
        );
    }
    
    @PostMapping("/{attendanceRecordId}/justification") 
    public ResponseEntity<?> justify(
            @PathVariable Long attendanceRecordId, 
            @Valid @RequestBody JustifyAttendanceResource r) {
        var result = commandService.handle(new JustifyAttendanceCommand(attendanceRecordId, r.reason(), r.evidenceUrl()));
        return ResponseEntityAssembler.toResponseEntityFromResult(
            result, 
            AttendanceRecordResourceFromEntityAssembler::toResourceFromEntity, 
            HttpStatus.OK
        );
    }
    
    @PostMapping("/{employeeId}/build/{workDate}") 
    public ResponseEntity<?> build(
            @PathVariable Long employeeId, 
            @PathVariable LocalDate workDate) {
        var result = commandService.handle(new BuildDailyAttendanceCommand(employeeId, workDate));
        return ResponseEntityAssembler.toResponseEntityFromResult(
            result, 
            AttendanceRecordResourceFromEntityAssembler::toResourceFromEntity, 
            HttpStatus.CREATED
        );
    }
    
    @GetMapping("/reports/employees/{employeeId}/hours") 
    public ResponseEntity<?> getEmployeeHours(
            @PathVariable Long employeeId, 
            @RequestParam LocalDate from, 
            @RequestParam LocalDate to) {
        if (from.isAfter(to)) {
            return ResponseEntity.badRequest().body("from must be before or equal to to");
        }
        
        var rows = queryService.handle(new GetAttendanceRecordsByEmployeeIdQuery(employeeId, from, to));
        double worked = rows.stream().mapToDouble(r -> r.getWorkedHours().toDecimalHours()).sum();
        double overtime = rows.stream().mapToDouble(r -> r.getWorkedHours().overtimeDecimalHours()).sum();
        
        return ResponseEntity.ok(new AttendanceHoursSummaryResource(employeeId, worked, overtime));
    }
    
    @GetMapping("/reports/areas/{areaId}") 
    public ResponseEntity<?> getAreaSummary(
            @PathVariable Long areaId, 
            @RequestParam LocalDate from, 
            @RequestParam LocalDate to) {
        if (from.isAfter(to)) {
            return ResponseEntity.badRequest().body("from must be before or equal to to");
        }
        
        return ResponseEntity.ok(queryService.getAreaSummary(areaId, from, to));
    }
}