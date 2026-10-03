package com.performily.flowboard.wellbeing.domain.model.valueobjects;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

/**
 * Health level of an environmental condition, ordered from best to worst.
 */
public enum HealthIndicator {
    OPTIMAL(1),
    ACCEPTABLE(2),
    POOR(3),
    HAZARDOUS(4);

    private final int severity;

    HealthIndicator(int severity) {
        this.severity = severity;
    }

    public int severity() { return severity; }

    public boolean isWorseThan(HealthIndicator other) {
        return other == null || this.severity > other.severity;
    }

    /**
     * Returns the worst indicator of the collection, or null when it has none.
     */
    public static HealthIndicator worstOf(Collection<HealthIndicator> indicators) {
        return indicators.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(HealthIndicator::severity))
                .orElse(null);
    }
}
