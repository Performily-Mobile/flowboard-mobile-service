package com.performily.flowboard.wellbeing.interfaces.rest.transform;

import com.performily.flowboard.wellbeing.domain.model.entities.MetricThreshold;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.ThresholdRange;
import com.performily.flowboard.wellbeing.interfaces.rest.resources.DefineMetricThresholdResource;
import com.performily.flowboard.wellbeing.interfaces.rest.resources.MetricThresholdResource;
import com.performily.flowboard.wellbeing.interfaces.rest.resources.ThresholdRangeResource;

import java.util.List;

public class MetricThresholdResourceFromEntityAssembler {
    public static MetricThresholdResource toResourceFromEntity(MetricThreshold t) {
        return new MetricThresholdResource(t.getId(), t.getOfficeId(), t.getMetricType(), t.getMetricType().unit(),
                t.getRanges().stream()
                        .map(r -> new ThresholdRangeResource(r.indicator(), r.minValue(), r.maxValue()))
                        .toList());
    }

    /** Converts the request ranges; invalid ranges throw IllegalArgumentException (400). */
    public static List<ThresholdRange> toRangesFromResource(DefineMetricThresholdResource r) {
        return r.ranges().stream()
                .map(x -> new ThresholdRange(x.indicator(), x.minValue(), x.maxValue()))
                .toList();
    }
}
