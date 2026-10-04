package com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.entities.EnvironmentalReadingPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EnvironmentalReadingPersistenceRepository extends JpaRepository<EnvironmentalReadingPersistenceEntity, Long> {
    Optional<EnvironmentalReadingPersistenceEntity> findFirstByOfficeIdAndMetricTypeOrderByRecordedAtDesc(Long officeId, MetricType metricType);
    Optional<EnvironmentalReadingPersistenceEntity> findFirstByDeviceIdOrderByRecordedAtDesc(Long deviceId);
    List<EnvironmentalReadingPersistenceEntity> findAllByOfficeIdAndMetricTypeAndRecordedAtBetweenOrderByRecordedAtAsc(
            Long officeId, MetricType metricType, LocalDateTime from, LocalDateTime to);
}
