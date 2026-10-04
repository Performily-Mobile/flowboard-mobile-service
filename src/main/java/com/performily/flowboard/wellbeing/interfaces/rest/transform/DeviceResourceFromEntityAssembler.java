package com.performily.flowboard.wellbeing.interfaces.rest.transform;

import com.performily.flowboard.wellbeing.application.queryservices.views.DeviceView;
import com.performily.flowboard.wellbeing.domain.model.entities.Device;
import com.performily.flowboard.wellbeing.interfaces.rest.resources.DeviceResource;

public class DeviceResourceFromEntityAssembler {
    public static DeviceResource toResourceFromEntity(Device d) {
        return new DeviceResource(d.getId(), d.getCode().value(), d.getSupportedMetrics(), d.getStatus(), d.getOfficeId(), null);
    }

    public static DeviceResource toResourceFromView(DeviceView v) {
        var d = v.device();
        return new DeviceResource(d.getId(), d.getCode().value(), d.getSupportedMetrics(), d.getStatus(), d.getOfficeId(), v.lastReadingAt());
    }
}
