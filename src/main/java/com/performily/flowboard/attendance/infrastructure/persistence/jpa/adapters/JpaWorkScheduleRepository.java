package com.performily.flowboard.attendance.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.attendance.domain.model.entities.WorkSchedule; 
import com.performily.flowboard.attendance.domain.model.valueobjects.*; 
import com.performily.flowboard.attendance.domain.repositories.WorkScheduleRepository; 
import com.performily.flowboard.attendance.infrastructure.persistence.jpa.entities.WorkSchedulePersistenceEntity; 
import com.performily.flowboard.attendance.infrastructure.persistence.jpa.repositories.WorkSchedulePersistenceRepository; 
import org.springframework.stereotype.Repository; 

import java.util.*;

@Repository 
public class JpaWorkScheduleRepository implements WorkScheduleRepository { 
    
    private final WorkSchedulePersistenceRepository repository; 

    public JpaWorkScheduleRepository(WorkSchedulePersistenceRepository repository) {
        this.repository = repository;
    } 

    private WorkSchedule toDomain(WorkSchedulePersistenceEntity e) {
        return new WorkSchedule(
            e.getId(),
            e.getPositionId(),
            new TimeRange(e.getStartTime(), e.getEndTime()),
            new LateTolerance(e.getToleranceMinutes()),
            e.getDays()
        );
    } 

    private WorkSchedulePersistenceEntity toEntity(WorkSchedule s) {
        var e = new WorkSchedulePersistenceEntity();
        e.setId(s.getId());
        e.setPositionId(s.getPositionId());
        e.setStartTime(s.getTimeRange().startTime());
        e.setEndTime(s.getTimeRange().endTime());
        e.setToleranceMinutes(s.getTolerance().minutes());
        e.setDays(new HashSet<>(s.getDays()));
        return e;
    } 

    public WorkSchedule save(WorkSchedule s) {
        var e = repository.save(toEntity(s));
        s.setId(e.getId());
        return s;
    } 

    public Optional<WorkSchedule> findByPositionId(Long p) {
        return repository.findByPositionId(p).map(this::toDomain);
    } 

    public Optional<WorkSchedule> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    } 

    public List<WorkSchedule> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }
}