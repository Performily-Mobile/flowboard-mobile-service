package com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.entities.MetricThresholdPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MetricThresholdPersistenceRepository extends JpaRepository<MetricThresholdPersistenceEntity, Long> {
    Optional<MetricThresholdPersistenceEntity> findByOfficeIdAndMetricType(Long officeId, MetricType metricType);
    List<MetricThresholdPersistenceEntity> findAllByOfficeId(Long officeId);
}
