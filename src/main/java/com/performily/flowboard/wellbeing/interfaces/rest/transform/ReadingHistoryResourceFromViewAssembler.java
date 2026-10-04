package com.performily.flowboard.wellbeing.interfaces.rest.transform;

import com.performily.flowboard.wellbeing.application.queryservices.views.ReadingHistoryView;
import com.performily.flowboard.wellbeing.interfaces.rest.resources.ReadingHistoryResource;

public class ReadingHistoryResourceFromViewAssembler {
    public static ReadingHistoryResource toResourceFromView(ReadingHistoryView v) {
        return new ReadingHistoryResource(v.officeId(), v.metricType(), v.metricType().unit(), v.from(), v.to(),
                v.minimum(), v.maximum(), v.average(), v.daysAboveAcceptable(),
                v.dailyAverages().stream()
                        .map(d -> new ReadingHistoryResource.DailyAverageResource(d.date(), d.average(), d.indicator()))
                        .toList(),
                v.readings().stream().map(ReadingResourceFromEntityAssembler::toResourceFromEntity).toList(),
                v.isEmpty() ? "There are no readings in the selected date range" : null);
    }
}
