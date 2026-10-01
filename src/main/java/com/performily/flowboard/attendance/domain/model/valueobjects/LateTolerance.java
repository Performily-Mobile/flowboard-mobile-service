package com.performily.flowboard.attendance.domain.model.valueobjects;

public record LateTolerance(int minutes) {
    public LateTolerance {
        if (minutes < 0) throw new IllegalArgumentException("Tolerance cannot be negative");
    }
    public boolean allows(long delayInMinutes) { return delayInMinutes <= minutes; }
}