package com.performily.flowboard.wellbeing.domain.repositories;

import com.performily.flowboard.wellbeing.domain.model.entities.EnvironmentalReading;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EnvironmentalReadingRepository {
    EnvironmentalReading save(EnvironmentalReading reading);
    Optional<EnvironmentalReading> findLatestByOfficeIdAndMetricType(Long officeId, MetricType metricType);
    Optional<EnvironmentalReading> findLatestByDeviceId(Long deviceId);
    List<EnvironmentalReading> findAllByOfficeIdAndMetricTypeBetween(Long officeId, MetricType metricType,
                                                                     LocalDateTime from, LocalDateTime to);
}
