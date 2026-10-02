package com.performily.flowboard.attendance.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.attendance.domain.model.entities.Punch; 
import com.performily.flowboard.attendance.domain.repositories.PunchRepository; 
import com.performily.flowboard.attendance.infrastructure.persistence.jpa.entities.PunchPersistenceEntity; 
import com.performily.flowboard.attendance.infrastructure.persistence.jpa.repositories.PunchPersistenceRepository; 
import org.springframework.stereotype.Repository; 

import java.time.*; 
import java.util.*;

@Repository 
public class JpaPunchRepository implements PunchRepository { 
    
    private final PunchPersistenceRepository repository; 

    public JpaPunchRepository(PunchPersistenceRepository repository) {
        this.repository = repository;
    } 

    private Punch toDomain(PunchPersistenceEntity e) {
        return new Punch(
            e.getId(),
            e.getEmployeeId(),
            e.getPunchedAt(),
            e.getType(),
            e.getAttendanceRecordId()
        );
    } 

    private PunchPersistenceEntity toEntity(Punch p) {
        var e = new PunchPersistenceEntity();
        e.setId(p.getId());
        e.setEmployeeId(p.getEmployeeId());
        e.setPunchedAt(p.getPunchedAt());
        e.setType(p.getType());
        e.setAttendanceRecordId(p.getAttendanceRecordId());
        return e;
    } 

    public Punch save(Punch p) {
        var e = repository.save(toEntity(p));
        p.setId(e.getId());
        return p;
    } 

    public List<Punch> findAllByEmployeeIdAndPunchedAtBetween(Long id, LocalDateTime f, LocalDateTime t) {
        return repository.findAllByEmployeeIdAndPunchedAtBetween(id, f, t).stream().map(this::toDomain).toList();
    } 

    public List<Punch> findAllByEmployeeIdAndDate(Long id, LocalDate d) {
        return findAllByEmployeeIdAndPunchedAtBetween(id, d.atStartOfDay(), d.plusDays(1).atStartOfDay().minusNanos(1));
    }
}