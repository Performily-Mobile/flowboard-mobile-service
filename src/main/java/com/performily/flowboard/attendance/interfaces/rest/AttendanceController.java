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
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import com.performily.flowboard.iam.domain.model.aggregates.UserAccount;
import com.performily.flowboard.attendance.domain.repositories.AttendanceRecordRepository;
import com.performily.flowboard.attendance.domain.model.valueobjects.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RestController 
@RequestMapping("/api/v1/attendance") 
@Tag(name="Attendance") 
public class AttendanceController {
    
    private final AttendanceCommandService commandService; 
    private final AttendanceQueryService queryService;
    private final AttendanceRecordRepository records; 

    public AttendanceController(AttendanceCommandService c, AttendanceQueryService q, AttendanceRecordRepository r) {
        commandService = c;
        queryService = q;
        records = r;
    }
    
    private UserAccount current(Authentication a) {
        if (a == null || !(a.getPrincipal() instanceof UserAccount u))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return u;
    }

    private void requireHr(Authentication a) {
        if (current(a).role != UserAccount.Role.HR_STAFF)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    private void requireOwnOrHr(Authentication a, Long employeeId) {
        var u = current(a);
        if (u.role != UserAccount.Role.HR_STAFF && !u.employeeId.equals(employeeId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    private void checkPeriod(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El inicio no puede ser posterior al fin.");
    }

    @GetMapping("/records")
    @Operation(summary="Paginated attendance records for HR, across all areas")
    public Page<AttendanceRecordResource> getRecords(
            Authentication auth, @RequestParam LocalDate from, @RequestParam LocalDate to,
            @RequestParam(required=false) AttendanceStatus status,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {
        requireHr(auth);
        checkPeriod(from, to);
        if (page < 0 || size < 1 || size > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paginación inválida.");
        return queryService.getRecords(from, to, status,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "workDate").and(Sort.by(Sort.Direction.DESC, "id"))))
            .map(AttendanceRecordResourceFromEntityAssembler::toResourceFromEntity);
    }

    @GetMapping("/areas/{areaId}/records")
    @Operation(summary="Attendance records for an area in a date range")
    public List<AttendanceRecordResource> getAreaRecords(Authentication auth,
            @PathVariable Long areaId, @RequestParam LocalDate from, @RequestParam LocalDate to) {
        requireHr(auth);
        checkPeriod(from, to);
        return queryService.getAreaRecords(areaId, from, to).stream()
                .map(AttendanceRecordResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @PostMapping("/punches") 
    @Operation(summary="Register attendance punch") 
    public ResponseEntity<?> registerPunch(Authentication auth, @Valid @RequestBody RegisterPunchResource resource) {
        if (!current(auth).employeeId.equals(resource.employeeId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puedes registrar tu propia asistencia.");
        var result = commandService.handle(new RegisterPunchCommand(
                current(auth).employeeId,
                java.time.LocalDateTime.now(java.time.ZoneId.of("America/Lima")),
                resource.type()));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, id -> id, HttpStatus.CREATED);
    }
    
    @GetMapping("/employees/{employeeId}") 
    @Operation(summary="Get attendance history by employee") 
    public ResponseEntity<List<AttendanceRecordResource>> getByEmployee(
            Authentication auth, @PathVariable Long employeeId, 
            @RequestParam LocalDate from, 
            @RequestParam LocalDate to) {
        requireOwnOrHr(auth, employeeId);
        checkPeriod(from, to);
        return ResponseEntity.ok(
            queryService.handle(new GetAttendanceRecordsByEmployeeIdQuery(employeeId, from, to)).stream()
                .map(AttendanceRecordResourceFromEntityAssembler::toResourceFromEntity)
                .toList()
        );
    }
    
    @GetMapping("/me") 
    public ResponseEntity<List<AttendanceRecordResource>> getMyAttendance(
            Authentication auth, @RequestParam LocalDate from, @RequestParam LocalDate to) {
        return getByEmployee(auth, current(auth).employeeId, from, to);
    }
    
    @GetMapping("/areas/{areaId}") 
    public ResponseEntity<List<AttendanceRecordResource>> getByArea(
            Authentication auth, @PathVariable Long areaId, 
            @RequestParam LocalDate workDate) {
        requireHr(auth);
        return ResponseEntity.ok(
            queryService.handle(new GetDailyAttendanceByAreaQuery(areaId, workDate)).stream()
                .map(AttendanceRecordResourceFromEntityAssembler::toResourceFromEntity)
                .toList()
        );
    }
    
    @PostMapping("/{attendanceRecordId}/justification") 
    public ResponseEntity<?> justify(
            Authentication auth, @PathVariable Long attendanceRecordId,
            @Valid @RequestBody JustifyAttendanceResource r) {
        var target = records.findById(attendanceRecordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireOwnOrHr(auth, target.getEmployeeId());
        var result = commandService.handle(new JustifyAttendanceCommand(attendanceRecordId, r.reason(), r.evidenceUrl()));
        return ResponseEntityAssembler.toResponseEntityFromResult(
            result, 
            AttendanceRecordResourceFromEntityAssembler::toResourceFromEntity, 
            HttpStatus.OK
        );
    }
    
    @PostMapping("/{employeeId}/build/{workDate}") 
    public ResponseEntity<?> build(
            Authentication auth, @PathVariable Long employeeId,
            @PathVariable LocalDate workDate) {
        requireHr(auth);
        var result = commandService.handle(new BuildDailyAttendanceCommand(employeeId, workDate));
        return ResponseEntityAssembler.toResponseEntityFromResult(
            result, 
            AttendanceRecordResourceFromEntityAssembler::toResourceFromEntity, 
            HttpStatus.CREATED
        );
    }
    
    @GetMapping("/reports/employees/{employeeId}/hours") 
    public ResponseEntity<?> getEmployeeHours(
            Authentication auth, @PathVariable Long employeeId, 
            @RequestParam LocalDate from, 
            @RequestParam LocalDate to) {
        requireHr(auth);
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
            Authentication auth, @PathVariable Long areaId, 
            @RequestParam LocalDate from, 
            @RequestParam LocalDate to) {
        requireHr(auth);
        if (from.isAfter(to)) {
            return ResponseEntity.badRequest().body("from must be before or equal to to");
        }
        
        return ResponseEntity.ok(queryService.getAreaSummary(areaId, from, to));
    }
}