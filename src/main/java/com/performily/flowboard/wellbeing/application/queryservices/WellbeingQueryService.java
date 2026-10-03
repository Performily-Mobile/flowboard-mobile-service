package com.performily.flowboard.wellbeing.application.queryservices;

import com.performily.flowboard.wellbeing.application.queryservices.views.OfficeStatusView;
import com.performily.flowboard.wellbeing.application.queryservices.views.ReadingHistoryView;
import com.performily.flowboard.wellbeing.domain.model.entities.Device;
import com.performily.flowboard.wellbeing.domain.model.entities.MetricThreshold;
import com.performily.flowboard.wellbeing.domain.model.queries.GetOfficeStatusQuery;
import com.performily.flowboard.wellbeing.domain.model.queries.GetReadingHistoryQuery;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceStatus;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;

import java.util.List;
import java.util.Optional;

public interface WellbeingQueryService {
    List<OfficeStatusView> getAllOfficeStatuses();
    Optional<OfficeStatusView> handle(GetOfficeStatusQuery query);
    Result<ReadingHistoryView, ApplicationError> handle(GetReadingHistoryQuery query);
    List<Device> getDevices(DeviceStatus status);
    List<MetricThreshold> getThresholdsByOfficeId(Long officeId);
}
