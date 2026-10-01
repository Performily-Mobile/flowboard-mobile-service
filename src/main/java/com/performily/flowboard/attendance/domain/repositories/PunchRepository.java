package com.performily.flowboard.attendance.domain.repositories;
import com.performily.flowboard.attendance.domain.model.entities.Punch; import java.time.*; import java.util.*;

public interface PunchRepository { 
    Punch save(Punch punch); 
    List<Punch> findAllByEmployeeIdAndPunchedAtBetween(Long employeeId, LocalDateTime from, LocalDateTime to); 
    List<Punch> findAllByEmployeeIdAndDate(Long employeeId, LocalDate date); 
}
