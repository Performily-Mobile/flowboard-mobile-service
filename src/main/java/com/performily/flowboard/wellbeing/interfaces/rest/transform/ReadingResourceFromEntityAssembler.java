package com.performily.flowboard.wellbeing.interfaces.rest.transform;

import com.performily.flowboard.wellbeing.domain.model.entities.EnvironmentalReading;
import com.performily.flowboard.wellbeing.interfaces.rest.resources.ReadingResource;

public class ReadingResourceFromEntityAssembler {
    public static ReadingResource toResourceFromEntity(EnvironmentalReading r) {
        return new ReadingResource(r.getId(), r.getOfficeId(), r.getDeviceId(), r.getMeasurement().metricType(),
                r.getMeasurement().value(), r.getMeasurement().unit(), r.getRecordedAt(), r.getHealthIndicator());
    }
}
