package com.performily.flowboard.wellbeing.domain.repositories;

import com.performily.flowboard.wellbeing.domain.model.entities.MetricThreshold;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import java.util.List;
import java.util.Optional;

public interface MetricThresholdRepository {
    MetricThreshold save(MetricThreshold threshold);
    Optional<MetricThreshold> findByOfficeIdAndMetricType(Long officeId, MetricType metricType);
    List<MetricThreshold> findAllByOfficeId(Long officeId);
}
