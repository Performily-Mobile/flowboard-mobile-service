package com.performily.flowboard.wellbeing.domain.model.entities;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceCode;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceStatus;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;

import java.util.EnumSet;
import java.util.Set;

/**
 * A measuring device registered in the inventory.
 * It can be linked to only one office at a time.
 */
public class Device {
    private Long id;
    private final DeviceCode code;
    private final Set<MetricType> supportedMetrics;
    private DeviceStatus status;
    private Long officeId;

    public Device(DeviceCode code, Set<MetricType> supportedMetrics) {
        if (code == null)
            throw new IllegalArgumentException("Device code is required");
        if (supportedMetrics == null || supportedMetrics.isEmpty())
            throw new IllegalArgumentException("A device must support at least one metric");
        this.code = code;
        this.supportedMetrics = EnumSet.copyOf(supportedMetrics);
        this.status = DeviceStatus.IN_INVENTORY;
        this.officeId = null;
    }

    public Device(Long id, DeviceCode code, Set<MetricType> supportedMetrics, DeviceStatus status, Long officeId) {
        this(code, supportedMetrics);
        this.id = id;
        this.status = status;
        this.officeId = officeId;
    }

    /**
     * Links the device to an office. Only devices in inventory can be linked.
     */
    public void linkTo(Long officeId) {
        if (officeId == null)
            throw new IllegalArgumentException("Office id is required");
        if (status == DeviceStatus.LINKED)
            throw new IllegalStateException("Device %s is already linked to an office".formatted(code.value()));
        if (status == DeviceStatus.INACTIVE)
            throw new IllegalStateException("Device %s is inactive".formatted(code.value()));
        this.officeId = officeId;
        this.status = DeviceStatus.LINKED;
    }

    /**
     * Unlinks the device and returns it to the inventory.
     */
    public void unlink() {
        if (status != DeviceStatus.LINKED)
            throw new IllegalStateException("Device %s is not linked".formatted(code.value()));
        this.officeId = null;
        this.status = DeviceStatus.IN_INVENTORY;
    }

    public boolean isLinkedTo(Long officeId) {
        return status == DeviceStatus.LINKED && officeId != null && officeId.equals(this.officeId);
    }

    public boolean canSendReadings() {
        return status == DeviceStatus.LINKED;
    }

    public boolean supports(MetricType metricType) {
        return supportedMetrics.contains(metricType);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DeviceCode getCode() { return code; }
    public Set<MetricType> getSupportedMetrics() { return Set.copyOf(supportedMetrics); }
    public DeviceStatus getStatus() { return status; }
    public Long getOfficeId() { return officeId; }
}
