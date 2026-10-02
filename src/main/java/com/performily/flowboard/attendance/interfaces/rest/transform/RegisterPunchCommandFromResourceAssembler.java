package com.performily.flowboard.attendance.interfaces.rest.transform;

import com.performily.flowboard.attendance.domain.model.commands.RegisterPunchCommand; 
import com.performily.flowboard.attendance.interfaces.rest.resources.RegisterPunchResource;

public class RegisterPunchCommandFromResourceAssembler { 
    
    public static RegisterPunchCommand toCommandFromResource(RegisterPunchResource r) {
        return new RegisterPunchCommand(
            r.employeeId(),
            r.punchedAt(),
            r.type()
        );
    } 
}