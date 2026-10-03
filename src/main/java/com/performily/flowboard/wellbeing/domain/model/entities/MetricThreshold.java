package com.performily.flowboard.wellbeing.domain.model.entities;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.HealthIndicator;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.ThresholdRange;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Ranges that classify the readings of one metric in one office.
 * Invariants:
 * - One threshold per office and metric type.
 * - Between 2 and 4 ranges, at most one range per health indicator.
 * - Ranges must be contiguous: no gaps and no overlaps.
 * - New ranges apply immediately to incoming readings.
 */
public class MetricThreshold {
    private Long id;
    private final Long officeId;
    private final MetricType metricType;
    private List<ThresholdRange> ranges;

    public MetricThreshold(Long officeId, MetricType metricType, List<ThresholdRange> ranges) {
        if (officeId == null)
            throw new IllegalArgumentException("Office id is required");
        if (metricType == null)
            throw new IllegalArgumentException("Metric type is required");
        this.officeId = officeId;
        this.metricType = metricType;
        this.ranges = validate(metricType, ranges);
    }

    public MetricThreshold(Long id, Long officeId, MetricType metricType, List<ThresholdRange> ranges) {
        this(officeId, metricType, ranges);
        this.id = id;
    }

    /**
     * Replaces the ranges of the threshold.
     */
    public void redefine(List<ThresholdRange> newRanges) {
        this.ranges = validate(metricType, newRanges);
    }

    /**
     * Classifies a value. Values below the first range or above the last one
     * take the indicator of the closest range.
     */
    public HealthIndicator classify(BigDecimal value) {
        for (ThresholdRange range : ranges) {
            if (range.contains(value)) return range.indicator();
        }
        var first = ranges.getFirst();
        return value.compareTo(first.minValue()) < 0 ? first.indicator() : ranges.getLast().indicator();
    }

    public Optional<ThresholdRange> rangeOf(HealthIndicator indicator) {
        return ranges.stream().filter(r -> r.indicator() == indicator).findFirst();
    }

    private static List<ThresholdRange> validate(MetricType metricType, List<ThresholdRange> ranges) {
        if (ranges == null || ranges.size() < 2 || ranges.size() > 4)
            throw new IllegalArgumentException("A threshold must have between 2 and 4 ranges");

        var indicators = EnumSet.noneOf(HealthIndicator.class);
        for (ThresholdRange range : ranges) {
            if (range == null)
                throw new IllegalArgumentException("Ranges cannot be null");
            if (!indicators.add(range.indicator()))
                throw new IllegalArgumentException("Only one range per indicator is allowed: %s is repeated".formatted(range.indicator()));
            if (!metricType.isWithinPhysicalRange(range.minValue()) || !metricType.isWithinPhysicalRange(range.maxValue()))
                throw new IllegalArgumentException("Range %s is outside the physical range of %s (%s to %s %s)".formatted(
                        range.indicator(), metricType, metricType.physicalMin(), metricType.physicalMax(), metricType.unit()));
        }

        var sorted = new ArrayList<>(ranges);
        sorted.sort(Comparator.comparing(ThresholdRange::minValue));

        for (int i = 0; i < sorted.size() - 1; i++) {
            var current = sorted.get(i);
            var next = sorted.get(i + 1);
            if (current.overlaps(next))
                throw new IllegalArgumentException("Ranges overlap: %s (%s-%s) and %s (%s-%s)".formatted(
                        current.indicator(), current.minValue(), current.maxValue(),
                        next.indicator(), next.minValue(), next.maxValue()));
            if (!current.isContiguousWith(next))
                throw new IllegalArgumentException("There is a gap between %s (ends at %s) and %s (starts at %s)".formatted(
                        current.indicator(), current.maxValue(), next.indicator(), next.minValue()));
        }
        return List.copyOf(sorted);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOfficeId() { return officeId; }
    public MetricType getMetricType() { return metricType; }
    public List<ThresholdRange> getRanges() { return ranges; }
}
