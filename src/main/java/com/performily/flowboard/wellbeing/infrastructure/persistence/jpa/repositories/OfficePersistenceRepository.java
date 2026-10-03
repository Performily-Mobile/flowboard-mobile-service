package com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.entities.OfficePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficePersistenceRepository extends JpaRepository<OfficePersistenceEntity, Long> {
    boolean existsByNameIgnoreCase(String name);
}
