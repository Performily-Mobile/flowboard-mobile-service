package com.performily.flowboard.wellbeing.infrastructure.simulation;

import com.performily.flowboard.wellbeing.application.commandservices.WellbeingCommandService;
import com.performily.flowboard.wellbeing.domain.model.commands.RegisterReadingCommand;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceStatus;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import com.performily.flowboard.wellbeing.domain.repositories.DeviceRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates test readings for every linked device.
 * In this version there are no physical sensors, readings are test data.
 * Disable it with wellbeing.simulator.enabled=false.
 */
@Component
@ConditionalOnProperty(name = "wellbeing.simulator.enabled", havingValue = "true", matchIfMissing = true)
public class EnvironmentalReadingSimulator {

    private final WellbeingCommandService commandService;
    private final DeviceRepository deviceRepository;

    public EnvironmentalReadingSimulator(WellbeingCommandService commandService, DeviceRepository deviceRepository) {
        this.commandService = commandService;
        this.deviceRepository = deviceRepository;
    }

    @Scheduled(initialDelayString = "${wellbeing.simulator.initial-delay-ms:30000}",
               fixedRateString = "${wellbeing.simulator.rate-ms:300000}")
    public void generateReadings() {
        deviceRepository.findAllByStatus(DeviceStatus.LINKED).forEach(device ->
                device.getSupportedMetrics().forEach(metric ->
                        commandService.handle(new RegisterReadingCommand(
                                device.getCode().value(), metric, randomValue(metric), null))));
    }

    private static BigDecimal randomValue(MetricType metric) {
        var random = ThreadLocalRandom.current();
        double value = switch (metric) {
            case TEMPERATURE -> random.nextDouble(18.0, 27.0);
            case ILLUMINATION -> random.nextDouble(350.0, 800.0);
            case AIR_QUALITY -> random.nextDouble(450.0, 1700.0);
        };
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP);
    }
}
