package com.performily.flowboard.wellbeing.interfaces.rest.transform;

import com.performily.flowboard.wellbeing.application.queryservices.views.MetricStatusView;
import com.performily.flowboard.wellbeing.application.queryservices.views.OfficeStatusView;
import com.performily.flowboard.wellbeing.interfaces.rest.resources.MetricStatusResource;
import com.performily.flowboard.wellbeing.interfaces.rest.resources.OfficeStatusResource;

public class OfficeStatusResourceFromViewAssembler {
    public static OfficeStatusResource toResourceFromView(OfficeStatusView v) {
        var o = v.office();
        return new OfficeStatusResource(o.getId(), o.getName(), o.getArea(), o.getLocation().address(),
                o.getLocation().floor(), o.getLocation().reference(), o.isActive(),
                v.upToDate(), v.overallIndicator(), v.lastReadingAt(),
                v.metrics().stream().map(OfficeStatusResourceFromViewAssembler::toMetricResource).toList(),
                v.devices().stream().map(DeviceResourceFromEntityAssembler::toResourceFromView).toList());
    }

    private static MetricStatusResource toMetricResource(MetricStatusView m) {
        var optimal = m.optimalRange();
        return new MetricStatusResource(m.metricType(), m.metricType().unit(), m.lastValue(), m.lastRecordedAt(),
                m.upToDate(), m.indicator(),
                optimal == null ? null : optimal.minValue(),
                optimal == null ? null : optimal.maxValue());
    }
}
