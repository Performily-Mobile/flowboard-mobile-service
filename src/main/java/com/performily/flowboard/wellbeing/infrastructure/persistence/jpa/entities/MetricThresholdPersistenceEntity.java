package com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.embeddables.ThresholdRangeEmbeddable;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "metric_thresholds",
        uniqueConstraints = @UniqueConstraint(name = "uk_threshold_office_metric", columnNames = {"office_id", "metric_type"}))
public class MetricThresholdPersistenceEntity {

    public MetricThresholdPersistenceEntity() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", nullable = false, length = 20)
    private MetricType metricType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "threshold_ranges", joinColumns = @JoinColumn(name = "metric_threshold_id"))
    private List<ThresholdRangeEmbeddable> ranges = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long v) { id = v; }

    public Long getOfficeId() { return officeId; }
    public void setOfficeId(Long v) { officeId = v; }

    public MetricType getMetricType() { return metricType; }
    public void setMetricType(MetricType v) { metricType = v; }

    public List<ThresholdRangeEmbeddable> getRanges() { return ranges; }
    public void setRanges(List<ThresholdRangeEmbeddable> v) { ranges = v; }
}
