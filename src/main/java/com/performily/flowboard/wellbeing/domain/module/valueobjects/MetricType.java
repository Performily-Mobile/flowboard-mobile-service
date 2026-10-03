package com.performily.flowboard.wellbeing.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Environmental metrics a device can capture, with their unit and physical range.
 */
public enum MetricType {
    TEMPERATURE("°C", new BigDecimal("-50"), new BigDecimal("80")),
    ILLUMINATION("lx", BigDecimal.ZERO, new BigDecimal("100000")),
    AIR_QUALITY("ppm", BigDecimal.ZERO, new BigDecimal("5000"));

    private final String unit;
    private final BigDecimal physicalMin;
    private final BigDecimal physicalMax;

    MetricType(String unit, BigDecimal physicalMin, BigDecimal physicalMax) {
        this.unit = unit;
        this.physicalMin = physicalMin;
        this.physicalMax = physicalMax;
    }

    public String unit() { return unit; }
    public BigDecimal physicalMin() { return physicalMin; }
    public BigDecimal physicalMax() { return physicalMax; }

    public boolean isWithinPhysicalRange(BigDecimal value) {
        return value != null && value.compareTo(physicalMin) >= 0 && value.compareTo(physicalMax) <= 0;
    }
}
