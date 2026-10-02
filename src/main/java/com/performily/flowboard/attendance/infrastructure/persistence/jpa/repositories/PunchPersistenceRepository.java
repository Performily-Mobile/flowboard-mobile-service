package com.performily.flowboard.attendance.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.attendance.infrastructure.persistence.jpa.entities.PunchPersistenceEntity; 
import org.springframework.data.jpa.repository.JpaRepository; 
import java.time.*; 
import java.util.*;

public interface PunchPersistenceRepository extends JpaRepository<PunchPersistenceEntity, Long> {
    
    List<PunchPersistenceEntity> findAllByEmployeeIdAndPunchedAtBetween(Long employeeId, LocalDateTime from, LocalDateTime to);
}