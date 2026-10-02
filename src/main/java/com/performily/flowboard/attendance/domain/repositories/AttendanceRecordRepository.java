package com.performily.flowboard.attendance.domain.repositories;

import com.performily.flowboard.attendance.domain.model.aggregates.AttendanceRecord;
import java.time.LocalDate; 
import java.util.*;

public interface AttendanceRecordRepository {
    Optional<AttendanceRecord> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
    
    List<AttendanceRecord> findAllByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate from, LocalDate to);
    
    boolean existsByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
    
    List<AttendanceRecord> findAll();
    
    AttendanceRecord save(AttendanceRecord record);
    
    Optional<AttendanceRecord> findById(Long id);
}