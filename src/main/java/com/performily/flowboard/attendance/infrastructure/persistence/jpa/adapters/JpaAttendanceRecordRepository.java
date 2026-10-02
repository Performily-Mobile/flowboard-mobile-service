package com.performily.flowboard.attendance.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.attendance.domain.model.aggregates.AttendanceRecord; 
import com.performily.flowboard.attendance.domain.model.valueobjects.WorkedHours; 
import com.performily.flowboard.attendance.domain.repositories.AttendanceRecordRepository; 
import com.performily.flowboard.attendance.infrastructure.persistence.jpa.entities.AttendanceRecordPersistenceEntity; 
import com.performily.flowboard.attendance.infrastructure.persistence.jpa.repositories.AttendanceRecordPersistenceRepository; 
import org.springframework.stereotype.Repository; 
import org.springframework.context.ApplicationEventPublisher; 

import java.time.Duration; 
import java.time.LocalDate; 
import java.util.*;

@Repository 
public class JpaAttendanceRecordRepository implements AttendanceRecordRepository {
    
    private final AttendanceRecordPersistenceRepository repository; 
    private final ApplicationEventPublisher eventPublisher; 

    public JpaAttendanceRecordRepository(AttendanceRecordPersistenceRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }
    
    private AttendanceRecord toDomain(AttendanceRecordPersistenceEntity e) {
        AttendanceRecord r = new AttendanceRecord(
            e.getEmployeeId(),
            e.getWorkDate(),
            e.getCheckInTime(),
            e.getCheckOutTime(),
            new WorkedHours(Duration.ofMinutes(e.getWorkedMinutes()), Duration.ofMinutes(e.getOvertimeMinutes())),
            e.getStatus(),
            e.getJustificationReason()
        );
        r.setId(e.getId());
        return r;
    }
    
    private AttendanceRecordPersistenceEntity toEntity(AttendanceRecord r) {
        var e = new AttendanceRecordPersistenceEntity();
        e.setId(r.getId());
        e.setEmployeeId(r.getEmployeeId());
        e.setWorkDate(r.getWorkDate());
        e.setCheckInTime(r.getCheckInTime());
        e.setCheckOutTime(r.getCheckOutTime());
        e.setWorkedMinutes(r.getWorkedHours().value().toMinutes());
        e.setOvertimeMinutes(r.getWorkedHours().overtime().toMinutes());
        e.setStatus(r.getStatus());
        e.setJustificationReason(r.getJustificationReason());
        return e;
    }
    
    public Optional<AttendanceRecord> findByEmployeeIdAndWorkDate(Long e, LocalDate d) {
        return repository.findByEmployeeIdAndWorkDate(e, d).map(this::toDomain);
    } 

    public List<AttendanceRecord> findAllByEmployeeIdAndWorkDateBetween(Long e, LocalDate f, LocalDate t) {
        return repository.findAllByEmployeeIdAndWorkDateBetween(e, f, t).stream().map(this::toDomain).toList();
    } 

    public boolean existsByEmployeeIdAndWorkDate(Long e, LocalDate d) {
        return repository.existsByEmployeeIdAndWorkDate(e, d);
    } 

    public List<AttendanceRecord> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    } 

    public Optional<AttendanceRecord> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    } 

    public AttendanceRecord save(AttendanceRecord r) {
        var saved = repository.save(toEntity(r));
        r.setId(saved.getId());
        for (var event : r.domainEvents()) {
            eventPublisher.publishEvent(event);
        }
        r.clearDomainEvents();
        return r;
    }
}