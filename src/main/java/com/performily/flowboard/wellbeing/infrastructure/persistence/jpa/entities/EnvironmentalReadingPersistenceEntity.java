package com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.HealthIndicator;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "environmental_readings",
        indexes = @Index(name = "idx_reading_office_metric_date", columnList = "office_id, metric_type, recorded_at"))
public class EnvironmentalReadingPersistenceEntity {

    public EnvironmentalReadingPersistenceEntity() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", nullable = false, length = 20)
    private MetricType metricType;

    @Column(name = "value", nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "health_indicator", length = 20)
    private HealthIndicator healthIndicator;

    public Long getId() { return id; }
    public void setId(Long v) { id = v; }

    public Long getOfficeId() { return officeId; }
    public void setOfficeId(Long v) { officeId = v; }

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long v) { deviceId = v; }

    public MetricType getMetricType() { return metricType; }
    public void setMetricType(MetricType v) { metricType = v; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal v) { value = v; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime v) { recordedAt = v; }

    public HealthIndicator getHealthIndicator() { return healthIndicator; }
    public void setHealthIndicator(HealthIndicator v) { healthIndicator = v; }
}
