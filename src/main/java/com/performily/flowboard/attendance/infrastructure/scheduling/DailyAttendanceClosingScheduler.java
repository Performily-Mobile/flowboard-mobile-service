package com.performily.flowboard.attendance.infrastructure.scheduling;

import com.performily.flowboard.attendance.application.commandservices.AttendanceCommandService; 
import com.performily.flowboard.attendance.domain.model.commands.BuildDailyAttendanceCommand; 
import com.performily.flowboard.workspace.application.facades.WorkspaceContextFacade; 
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.scheduling.annotation.Scheduled; 
import org.springframework.stereotype.Component;

/** 
 * Daily closing hook. The actual employee iteration is delegated to the application integration layer in deployment. 
 */
@Component 
public class DailyAttendanceClosingScheduler { 
    
    private final AttendanceCommandService service; 
    private final WorkspaceContextFacade workspace; 

    public DailyAttendanceClosingScheduler(AttendanceCommandService service, WorkspaceContextFacade workspace) {
        this.service = service;
        this.workspace = workspace;
    } 

    @Value("${attendance.closing.zone:America/Lima}")
    private String closingZone;

    @Scheduled(cron="0 5 23 * * *", zone="America/Lima") 
    public void runDailyClosing() { 
        workspace.findAllActiveEmployees().forEach(e -> 
            service.handle(new BuildDailyAttendanceCommand(e.getId(), LocalDate.now(ZoneId.of(closingZone))))
        ); 
    } 
}