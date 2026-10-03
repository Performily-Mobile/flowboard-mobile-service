package com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceStatus;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "devices")
public class DevicePersistenceEntity {

    public DevicePersistenceEntity() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DeviceStatus status;

    @Column(name = "office_id")
    private Long officeId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "device_supported_metrics", joinColumns = @JoinColumn(name = "device_id"))
    @Column(name = "metric_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Set<MetricType> supportedMetrics = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long v) { id = v; }

    public String getCode() { return code; }
    public void setCode(String v) { code = v; }

    public DeviceStatus getStatus() { return status; }
    public void setStatus(DeviceStatus v) { status = v; }

    public Long getOfficeId() { return officeId; }
    public void setOfficeId(Long v) { officeId = v; }

    public Set<MetricType> getSupportedMetrics() { return supportedMetrics; }
    public void setSupportedMetrics(Set<MetricType> v) { supportedMetrics = v; }
}
