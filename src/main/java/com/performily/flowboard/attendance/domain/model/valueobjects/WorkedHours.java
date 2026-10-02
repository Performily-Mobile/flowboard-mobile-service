package com.performily.flowboard.attendance.domain.model.valueobjects;

import java.time.Duration;

public record WorkedHours(Duration value, Duration overtime) {
    public WorkedHours {
        if (value == null || overtime == null) 
            throw new IllegalArgumentException("Worked hours cannot be null");
        if (value.isNegative() || overtime.isNegative()) 
            throw new IllegalArgumentException("Worked hours cannot be negative");
    }

    public static WorkedHours empty() { 
        return new WorkedHours(Duration.ZERO, Duration.ZERO); 
    }

    public double toDecimalHours() { 
        return value.toMinutes() / 60.0; 
    }

    public double overtimeDecimalHours() { 
        return overtime.toMinutes() / 60.0; 
    }

    public boolean isEmpty() { 
        return value.isZero(); 
    }
}
