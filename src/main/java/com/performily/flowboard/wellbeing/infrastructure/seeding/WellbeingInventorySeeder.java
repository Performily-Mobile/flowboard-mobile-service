package com.performily.flowboard.wellbeing.infrastructure.seeding;

import com.performily.flowboard.wellbeing.application.commandservices.WellbeingCommandService;
import com.performily.flowboard.wellbeing.domain.model.commands.RegisterDeviceCommand;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import com.performily.flowboard.wellbeing.domain.repositories.DeviceRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Loads the device inventory when the application starts and the inventory is empty.
 * Flowboard does not sell sensors: the inventory represents the devices the organization owns.
 */
@Component
public class WellbeingInventorySeeder {

    private static final Map<String, Set<MetricType>> DEVICES = Map.of(
            "ENV-0012", Set.of(MetricType.TEMPERATURE, MetricType.AIR_QUALITY),
            "LUX-0044", Set.of(MetricType.ILLUMINATION),
            "ENV-0019", Set.of(MetricType.TEMPERATURE, MetricType.AIR_QUALITY),
            "ENV-0021", Set.of(MetricType.TEMPERATURE, MetricType.AIR_QUALITY),
            "LUX-0050", Set.of(MetricType.ILLUMINATION),
            "ENV-0031", Set.of(MetricType.TEMPERATURE, MetricType.AIR_QUALITY),
            "LUX-0058", Set.of(MetricType.ILLUMINATION),
            "ENV-0040", Set.of(MetricType.TEMPERATURE, MetricType.AIR_QUALITY)
    );

    private final WellbeingCommandService commandService;
    private final DeviceRepository deviceRepository;

    public WellbeingInventorySeeder(WellbeingCommandService commandService, DeviceRepository deviceRepository) {
        this.commandService = commandService;
        this.deviceRepository = deviceRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedInventory() {
        if (!deviceRepository.findAll().isEmpty()) return;
        DEVICES.forEach((code, metrics) -> commandService.handle(new RegisterDeviceCommand(code, metrics)));
    }
}
