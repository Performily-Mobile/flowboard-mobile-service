package com.performily.flowboard.attendance.domain.model.valueobjects;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

public record TimeRange(LocalTime startTime, LocalTime endTime) {
    public TimeRange {
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        if (!endTime.isAfter(startTime)) 
            throw new IllegalArgumentException("End time must be after start time");
    }

    public boolean contains(LocalTime time) { 
        return !time.isBefore(startTime) && !time.isAfter(endTime); 
    }

    public long durationInMinutes() { 
        return Duration.between(startTime, endTime).toMinutes(); 
    }
}
