package com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.wellbeing.domain.model.entities.MetricThreshold;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.ThresholdRange;
import com.performily.flowboard.wellbeing.domain.repositories.MetricThresholdRepository;
import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.embeddables.ThresholdRangeEmbeddable;
import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.entities.MetricThresholdPersistenceEntity;
import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.repositories.MetricThresholdPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaMetricThresholdRepository implements MetricThresholdRepository {

    private final MetricThresholdPersistenceRepository repository;

    public JpaMetricThresholdRepository(MetricThresholdPersistenceRepository repository) {
        this.repository = repository;
    }

    private MetricThreshold toDomain(MetricThresholdPersistenceEntity e) {
        var ranges = e.getRanges().stream()
                .map(r -> new ThresholdRange(r.getIndicator(), r.getMinValue(), r.getMaxValue()))
                .toList();
        return new MetricThreshold(e.getId(), e.getOfficeId(), e.getMetricType(), ranges);
    }

    private MetricThresholdPersistenceEntity toEntity(MetricThreshold t) {
        var e = new MetricThresholdPersistenceEntity();
        e.setId(t.getId());
        e.setOfficeId(t.getOfficeId());
        e.setMetricType(t.getMetricType());
        e.setRanges(new ArrayList<>(t.getRanges().stream()
                .map(r -> new ThresholdRangeEmbeddable(r.indicator(), r.minValue(), r.maxValue()))
                .toList()));
        return e;
    }

    public MetricThreshold save(MetricThreshold t) {
        var e = repository.save(toEntity(t));
        t.setId(e.getId());
        return t;
    }

    public Optional<MetricThreshold> findByOfficeIdAndMetricType(Long officeId, MetricType metricType) {
        return repository.findByOfficeIdAndMetricType(officeId, metricType).map(this::toDomain);
    }

    public List<MetricThreshold> findAllByOfficeId(Long officeId) {
        return repository.findAllByOfficeId(officeId).stream().map(this::toDomain).toList();
    }
}
