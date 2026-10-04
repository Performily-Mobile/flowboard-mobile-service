package com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.embeddables;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.HealthIndicator;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Embeddable
public class ThresholdRangeEmbeddable {

    public ThresholdRangeEmbeddable() {}

    public ThresholdRangeEmbeddable(HealthIndicator indicator, BigDecimal minValue, BigDecimal maxValue) {
        this.indicator = indicator;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "indicator", nullable = false, length = 20)
    private HealthIndicator indicator;

    @Column(name = "min_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal minValue;

    @Column(name = "max_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal maxValue;

    public HealthIndicator getIndicator() { return indicator; }
    public BigDecimal getMinValue() { return minValue; }
    public BigDecimal getMaxValue() { return maxValue; }
}
