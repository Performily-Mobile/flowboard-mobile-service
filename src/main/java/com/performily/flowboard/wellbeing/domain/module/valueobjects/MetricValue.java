package com.performily.flowboard.wellbeing.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A measured value of a metric. It must be within the physical range of the metric.
 */
public record MetricValue(MetricType metricType, BigDecimal value) {
    public MetricValue {
        if (metricType == null)
            throw new IllegalArgumentException("Metric type is required");
        if (value == null)
            throw new IllegalArgumentException("Metric value is required");
        if (!metricType.isWithinPhysicalRange(value))
            throw new IllegalArgumentException("%s must be between %s and %s %s".formatted(
                    metricType, metricType.physicalMin(), metricType.physicalMax(), metricType.unit()));
        value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public String unit() {
        return metricType.unit();
    }
}
