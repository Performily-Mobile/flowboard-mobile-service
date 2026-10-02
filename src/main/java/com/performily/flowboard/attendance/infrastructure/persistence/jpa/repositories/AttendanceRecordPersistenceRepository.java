package com.performily.flowboard.attendance.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.attendance.infrastructure.persistence.jpa.entities.AttendanceRecordPersistenceEntity; 
import org.springframework.data.jpa.repository.JpaRepository; 
import java.time.LocalDate; 
import java.util.*;

public interface AttendanceRecordPersistenceRepository extends JpaRepository<AttendanceRecordPersistenceEntity, Long> {
    
    Optional<AttendanceRecordPersistenceEntity> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate); 
    
    List<AttendanceRecordPersistenceEntity> findAllByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate from, LocalDate to); 
    
    boolean existsByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
}