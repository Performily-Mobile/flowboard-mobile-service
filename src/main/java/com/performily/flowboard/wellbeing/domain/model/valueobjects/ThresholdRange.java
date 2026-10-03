package com.performily.flowboard.wellbeing.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Range of values that maps to a health indicator.
 * The range includes minValue and excludes maxValue: [minValue, maxValue).
 * That way two contiguous ranges share the boundary (e.g. 0-600 and 600-1000).
 */
public record ThresholdRange(HealthIndicator indicator, BigDecimal minValue, BigDecimal maxValue) {
    public ThresholdRange {
        if (indicator == null)
            throw new IllegalArgumentException("Range indicator is required");
        if (minValue == null || maxValue == null)
            throw new IllegalArgumentException("Range min and max values are required");
        if (maxValue.compareTo(minValue) <= 0)
            throw new IllegalArgumentException("Range %s: max value must be greater than min value".formatted(indicator));
    }

    public boolean contains(BigDecimal value) {
        return value.compareTo(minValue) >= 0 && value.compareTo(maxValue) < 0;
    }

    public boolean overlaps(ThresholdRange other) {
        return minValue.compareTo(other.maxValue) < 0 && other.minValue.compareTo(maxValue) < 0;
    }

    public boolean isContiguousWith(ThresholdRange next) {
        return maxValue.compareTo(next.minValue) == 0;
    }
}
