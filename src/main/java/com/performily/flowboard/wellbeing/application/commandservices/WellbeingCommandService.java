package com.performily.flowboard.wellbeing.application.commandservices;

import com.performily.flowboard.wellbeing.domain.model.aggregates.Office;
import com.performily.flowboard.wellbeing.domain.model.commands.*;
import com.performily.flowboard.wellbeing.domain.model.entities.Device;
import com.performily.flowboard.wellbeing.domain.model.entities.EnvironmentalReading;
import com.performily.flowboard.wellbeing.domain.model.entities.MetricThreshold;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;

public interface WellbeingCommandService {
    Result<Office, ApplicationError> handle(CreateOfficeCommand command);
    Result<Device, ApplicationError> handle(RegisterDeviceCommand command);
    Result<Device, ApplicationError> handle(LinkDeviceCommand command);
    Result<Device, ApplicationError> handle(UnlinkDeviceCommand command);
    Result<MetricThreshold, ApplicationError> handle(DefineMetricThresholdCommand command);
    Result<EnvironmentalReading, ApplicationError> handle(RegisterReadingCommand command);
}
