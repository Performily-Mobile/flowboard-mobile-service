package com.performily.flowboard.attendance.domain.repositories;

import com.performily.flowboard.attendance.domain.model.aggregates.AttendanceRecord;
import java.time.LocalDate; 
import java.util.*;

public interface AttendanceRecordRepository {
    Optional<AttendanceRecord> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
    
    List<AttendanceRecord> findAllByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate from, LocalDate to);
    
    boolean existsByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
    
    List<AttendanceRecord> findAllByEmployeeIdsAndWorkDate(Collection<Long> ids, LocalDate date);
    List<AttendanceRecord> findAllByEmployeeIdsAndWorkDateBetween(Collection<Long> ids, LocalDate from, LocalDate to);

    org.springframework.data.domain.Page<AttendanceRecord> findRecords(
        LocalDate from, LocalDate to, com.performily.flowboard.attendance.domain.model.valueobjects.AttendanceStatus status,
        org.springframework.data.domain.Pageable pageable);

    List<AttendanceRecord> findAll();
    
    AttendanceRecord save(AttendanceRecord record);
    
    Optional<AttendanceRecord> findById(Long id);
}