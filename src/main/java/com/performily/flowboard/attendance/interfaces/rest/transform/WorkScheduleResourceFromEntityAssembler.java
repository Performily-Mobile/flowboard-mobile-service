package com.performily.flowboard.attendance.interfaces.rest.transform;

import com.performily.flowboard.attendance.domain.model.entities.WorkSchedule; 
import com.performily.flowboard.attendance.interfaces.rest.resources.WorkScheduleResource;

public class WorkScheduleResourceFromEntityAssembler { 
    
    public static WorkScheduleResource toResourceFromEntity(WorkSchedule s) {
        return new WorkScheduleResource(
            s.getId(),
            s.getPositionId(),
            s.getTimeRange().startTime(),
            s.getTimeRange().endTime(),
            s.getTolerance().minutes(),
            s.getDays()
        );
    } 
}