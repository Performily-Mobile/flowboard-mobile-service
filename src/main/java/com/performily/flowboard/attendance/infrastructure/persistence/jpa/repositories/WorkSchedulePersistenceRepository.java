package com.performily.flowboard.attendance.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.attendance.infrastructure.persistence.jpa.entities.WorkSchedulePersistenceEntity; 
import org.springframework.data.jpa.repository.JpaRepository; 
import java.util.*;

public interface WorkSchedulePersistenceRepository extends JpaRepository<WorkSchedulePersistenceEntity, Long> {
    
    Optional<WorkSchedulePersistenceEntity> findByPositionId(Long positionId);
}