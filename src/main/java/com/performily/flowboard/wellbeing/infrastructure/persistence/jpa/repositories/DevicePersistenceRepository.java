package com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceStatus;
import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.entities.DevicePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DevicePersistenceRepository extends JpaRepository<DevicePersistenceEntity, Long> {
    Optional<DevicePersistenceEntity> findByCode(String code);
    List<DevicePersistenceEntity> findAllByStatus(DeviceStatus status);
    List<DevicePersistenceEntity> findAllByOfficeId(Long officeId);
    boolean existsByCode(String code);
}
