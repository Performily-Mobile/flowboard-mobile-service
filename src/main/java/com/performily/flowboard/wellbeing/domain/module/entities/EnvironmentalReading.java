package com.performily.flowboard.wellbeing.domain.model.entities;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.HealthIndicator;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricValue;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * A value captured by a device in an office at a given moment,
 * classified against the threshold in force when it arrived.
 */
public class EnvironmentalReading {
    private Long id;
    private final Long officeId;
    private final Long deviceId;
    private final MetricValue measurement;
    private final LocalDateTime recordedAt;
    private HealthIndicator healthIndicator;

    public EnvironmentalReading(Long officeId, Long deviceId, MetricValue measurement, LocalDateTime recordedAt) {
        if (officeId == null || deviceId == null)
            throw new IllegalArgumentException("Office and device are required");
        if (measurement == null)
            throw new IllegalArgumentException("Measurement is required");
        if (recordedAt == null)
            throw new IllegalArgumentException("Recorded date is required");
        if (recordedAt.isAfter(LocalDateTime.now().plusMinutes(1)))
            throw new IllegalArgumentException("Recorded date cannot be in the future");
        this.officeId = officeId;
        this.deviceId = deviceId;
        this.measurement = measurement;
        this.recordedAt = recordedAt;
    }

    public EnvironmentalReading(Long id, Long officeId, Long deviceId, MetricValue measurement,
                                LocalDateTime recordedAt, HealthIndicator healthIndicator) {
        this.id = id;
        this.officeId = officeId;
        this.deviceId = deviceId;
        this.measurement = measurement;
        this.recordedAt = recordedAt;
        this.healthIndicator = healthIndicator;
    }

    public void classify(MetricThreshold threshold) {
        if (threshold == null || threshold.getMetricType() != measurement.metricType()) return;
        this.healthIndicator = threshold.classify(measurement.value());
    }

    /**
     * A reading is recent when it is not older than the validity window.
     */
    public boolean isRecent(Duration validity, LocalDateTime now) {
        return !recordedAt.isBefore(now.minus(validity));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOfficeId() { return officeId; }
    public Long getDeviceId() { return deviceId; }
    public MetricValue getMeasurement() { return measurement; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public HealthIndicator getHealthIndicator() { return healthIndicator; }
}
