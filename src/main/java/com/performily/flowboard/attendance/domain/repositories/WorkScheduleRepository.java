package com.performily.flowboard.attendance.domain.repositories;
import com.performily.flowboard.attendance.domain.model.entities.WorkSchedule; import java.util.*;

public interface WorkScheduleRepository { 
    WorkSchedule save(WorkSchedule schedule); 
    Optional<WorkSchedule> findByPositionId(Long positionId); 
    Optional<WorkSchedule> findById(Long id); 
    List<WorkSchedule> findAll(); 
}
