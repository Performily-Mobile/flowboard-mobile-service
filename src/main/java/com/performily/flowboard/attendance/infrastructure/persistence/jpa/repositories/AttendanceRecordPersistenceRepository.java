package com.performily.flowboard.attendance.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.attendance.infrastructure.persistence.jpa.entities.AttendanceRecordPersistenceEntity; 
import org.springframework.data.jpa.repository.JpaRepository; 
import java.time.LocalDate; 
import java.util.*;

public interface AttendanceRecordPersistenceRepository extends JpaRepository<AttendanceRecordPersistenceEntity, Long> {
    
    Optional<AttendanceRecordPersistenceEntity> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate); 
    
    List<AttendanceRecordPersistenceEntity> findAllByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate from, LocalDate to); 
    
    List<AttendanceRecordPersistenceEntity> findAllByEmployeeIdInAndWorkDate(Collection<Long> ids, LocalDate date);
    List<AttendanceRecordPersistenceEntity> findAllByEmployeeIdInAndWorkDateBetween(Collection<Long> ids, LocalDate from, LocalDate to);

    @org.springframework.data.jpa.repository.Query("""
        select r from AttendanceRecordPersistenceEntity r
        where r.workDate between :from and :to
          and (:status is null or r.status = :status)
        """)
    org.springframework.data.domain.Page<AttendanceRecordPersistenceEntity> findGeneralRecords(
        @org.springframework.data.repository.query.Param("from") LocalDate from,
        @org.springframework.data.repository.query.Param("to") LocalDate to,
        @org.springframework.data.repository.query.Param("status") com.performily.flowboard.attendance.domain.model.valueobjects.AttendanceStatus status,
        org.springframework.data.domain.Pageable pageable);

    boolean existsByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
}