package com.performily.flowboard.wellbeing.application.internal.commandservices;

import com.performily.flowboard.wellbeing.application.commandservices.WellbeingCommandService;
import com.performily.flowboard.wellbeing.domain.model.aggregates.Office;
import com.performily.flowboard.wellbeing.domain.model.commands.*;
import com.performily.flowboard.wellbeing.domain.model.entities.Device;
import com.performily.flowboard.wellbeing.domain.model.entities.EnvironmentalReading;
import com.performily.flowboard.wellbeing.domain.model.entities.MetricThreshold;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceCode;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceStatus;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricValue;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.OfficeLocation;
import com.performily.flowboard.wellbeing.domain.repositories.DeviceRepository;
import com.performily.flowboard.wellbeing.domain.repositories.EnvironmentalReadingRepository;
import com.performily.flowboard.wellbeing.domain.repositories.MetricThresholdRepository;
import com.performily.flowboard.wellbeing.domain.repositories.OfficeRepository;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WellbeingCommandServiceImpl implements WellbeingCommandService {

    private final OfficeRepository officeRepository;
    private final DeviceRepository deviceRepository;
    private final MetricThresholdRepository thresholdRepository;
    private final EnvironmentalReadingRepository readingRepository;

    public WellbeingCommandServiceImpl(OfficeRepository officeRepository,
                                       DeviceRepository deviceRepository,
                                       MetricThresholdRepository thresholdRepository,
                                       EnvironmentalReadingRepository readingRepository) {
        this.officeRepository = officeRepository;
        this.deviceRepository = deviceRepository;
        this.thresholdRepository = thresholdRepository;
        this.readingRepository = readingRepository;
    }

    // US47 - Register a workspace
    @Override
    public Result<Office, ApplicationError> handle(CreateOfficeCommand c) {
        try {
            var office = new Office(c.name(), c.area(), new OfficeLocation(c.address(), c.floor(), c.reference()));
            if (officeRepository.existsByName(office.getName())) {
                return Result.failure(ApplicationError.conflict("Office", "An office named '%s' already exists".formatted(office.getName())));
            }
            return Result.success(officeRepository.save(office));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("office", e.getMessage()));
        }
    }

    // Inventory registration (used by the seeder and the devices endpoint)
    @Override
    public Result<Device, ApplicationError> handle(RegisterDeviceCommand c) {
        try {
            var device = new Device(new DeviceCode(c.code()), c.supportedMetrics());
            if (deviceRepository.existsByCode(device.getCode().value())) {
                return Result.failure(ApplicationError.conflict("Device", "Device %s is already registered".formatted(device.getCode().value())));
            }
            return Result.success(deviceRepository.save(device));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("device", e.getMessage()));
        }
    }

    // US48 - Link a device to a workspace
    @Override
    public Result<Device, ApplicationError> handle(LinkDeviceCommand c) {
        var office = officeRepository.findById(c.officeId());
        if (office.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Office", String.valueOf(c.officeId())));
        }
        if (!office.get().isActive()) {
            return Result.failure(ApplicationError.businessRuleViolation("office-active", "Devices can only be linked to active offices"));
        }
        if (c.deviceCode() == null || c.deviceCode().isBlank()) {
            return Result.failure(ApplicationError.validationError("deviceCode", "Device code is required"));
        }

        var device = deviceRepository.findByCode(c.deviceCode().trim().toUpperCase());
        if (device.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Device", c.deviceCode() + " is not registered in the inventory"));
        }
        if (device.get().getStatus() == DeviceStatus.LINKED) {
            var currentOffice = officeRepository.findById(device.get().getOfficeId())
                    .map(Office::getName).orElse("another office");
            return Result.failure(ApplicationError.conflict("Device",
                    "Device %s is already linked to %s".formatted(device.get().getCode().value(), currentOffice)));
        }

        try {
            device.get().linkTo(office.get().getId());
            return Result.success(deviceRepository.save(device.get()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("device-link", e.getMessage()));
        }
    }

    // US48 - Unlink a device (returns it to the inventory)
    @Override
    public Result<Device, ApplicationError> handle(UnlinkDeviceCommand c) {
        var device = deviceRepository.findByCode(c.deviceCode() == null ? "" : c.deviceCode().trim().toUpperCase());
        if (device.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Device", String.valueOf(c.deviceCode())));
        }
        if (!device.get().isLinkedTo(c.officeId())) {
            return Result.failure(ApplicationError.businessRuleViolation("device-unlink", "The device is not linked to this office"));
        }
        device.get().unlink();
        return Result.success(deviceRepository.save(device.get()));
    }

    // US49 - Define (or redefine) the ranges of a metric
    @Override
    public Result<MetricThreshold, ApplicationError> handle(DefineMetricThresholdCommand c) {
        if (officeRepository.findById(c.officeId()).isEmpty()) {
            return Result.failure(ApplicationError.notFound("Office", String.valueOf(c.officeId())));
        }
        if (c.metricType() == null) {
            return Result.failure(ApplicationError.validationError("metricType", "Metric type is required"));
        }
        try {
            var threshold = thresholdRepository.findByOfficeIdAndMetricType(c.officeId(), c.metricType())
                    .map(existing -> {
                        existing.redefine(c.ranges());
                        return existing;
                    })
                    .orElseGet(() -> new MetricThreshold(c.officeId(), c.metricType(), c.ranges()));
            return Result.success(thresholdRepository.save(threshold));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("ranges", e.getMessage()));
        }
    }

    // Test data: a linked device sends a reading, it is classified with the office threshold
    @Override
    public Result<EnvironmentalReading, ApplicationError> handle(RegisterReadingCommand c) {
        if (c.deviceCode() == null || c.deviceCode().isBlank()) {
            return Result.failure(ApplicationError.validationError("deviceCode", "Device code is required"));
        }
        var device = deviceRepository.findByCode(c.deviceCode().trim().toUpperCase());
        if (device.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Device", c.deviceCode()));
        }
        if (!device.get().canSendReadings()) {
            return Result.failure(ApplicationError.businessRuleViolation("device-readings", "Only linked devices can send readings"));
        }
        if (!device.get().supports(c.metricType())) {
            return Result.failure(ApplicationError.businessRuleViolation("device-metric",
                    "Device %s does not measure %s".formatted(device.get().getCode().value(), c.metricType())));
        }
        try {
            var officeId = device.get().getOfficeId();
            var recordedAt = c.recordedAt() != null ? c.recordedAt() : LocalDateTime.now();
            var reading = new EnvironmentalReading(officeId, device.get().getId(),
                    new MetricValue(c.metricType(), c.value()), recordedAt);
            thresholdRepository.findByOfficeIdAndMetricType(officeId, c.metricType()).ifPresent(reading::classify);
            return Result.success(readingRepository.save(reading));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("reading", e.getMessage()));
        }
    }
}
