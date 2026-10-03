package com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.wellbeing.domain.model.entities.EnvironmentalReading;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricValue;
import com.performily.flowboard.wellbeing.domain.repositories.EnvironmentalReadingRepository;
import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.entities.EnvironmentalReadingPersistenceEntity;
import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.repositories.EnvironmentalReadingPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaEnvironmentalReadingRepository implements EnvironmentalReadingRepository {

    private final EnvironmentalReadingPersistenceRepository repository;

    public JpaEnvironmentalReadingRepository(EnvironmentalReadingPersistenceRepository repository) {
        this.repository = repository;
    }

    private EnvironmentalReading toDomain(EnvironmentalReadingPersistenceEntity e) {
        return new EnvironmentalReading(e.getId(), e.getOfficeId(), e.getDeviceId(),
                new MetricValue(e.getMetricType(), e.getValue()), e.getRecordedAt(), e.getHealthIndicator());
    }

    private EnvironmentalReadingPersistenceEntity toEntity(EnvironmentalReading r) {
        var e = new EnvironmentalReadingPersistenceEntity();
        e.setId(r.getId());
        e.setOfficeId(r.getOfficeId());
        e.setDeviceId(r.getDeviceId());
        e.setMetricType(r.getMeasurement().metricType());
        e.setValue(r.getMeasurement().value());
        e.setRecordedAt(r.getRecordedAt());
        e.setHealthIndicator(r.getHealthIndicator());
        return e;
    }

    public EnvironmentalReading save(EnvironmentalReading r) {
        var e = repository.save(toEntity(r));
        r.setId(e.getId());
        return r;
    }

    public Optional<EnvironmentalReading> findLatestByOfficeIdAndMetricType(Long officeId, MetricType metricType) {
        return repository.findFirstByOfficeIdAndMetricTypeOrderByRecordedAtDesc(officeId, metricType).map(this::toDomain);
    }

    public Optional<EnvironmentalReading> findLatestByDeviceId(Long deviceId) {
        return repository.findFirstByDeviceIdOrderByRecordedAtDesc(deviceId).map(this::toDomain);
    }

    public List<EnvironmentalReading> findAllByOfficeIdAndMetricTypeBetween(Long officeId, MetricType metricType,
                                                                            LocalDateTime from, LocalDateTime to) {
        return repository.findAllByOfficeIdAndMetricTypeAndRecordedAtBetweenOrderByRecordedAtAsc(officeId, metricType, from, to)
                .stream().map(this::toDomain).toList();
    }
}
