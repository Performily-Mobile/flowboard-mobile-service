package com.performily.flowboard.wellbeing.application.internal.queryservices;

import com.performily.flowboard.wellbeing.application.queryservices.WellbeingQueryService;
import com.performily.flowboard.wellbeing.application.queryservices.views.*;
import com.performily.flowboard.wellbeing.domain.model.aggregates.Office;
import com.performily.flowboard.wellbeing.domain.model.entities.Device;
import com.performily.flowboard.wellbeing.domain.model.entities.EnvironmentalReading;
import com.performily.flowboard.wellbeing.domain.model.entities.MetricThreshold;
import com.performily.flowboard.wellbeing.domain.model.queries.GetOfficeStatusQuery;
import com.performily.flowboard.wellbeing.domain.model.queries.GetReadingHistoryQuery;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceStatus;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.HealthIndicator;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import com.performily.flowboard.wellbeing.domain.repositories.DeviceRepository;
import com.performily.flowboard.wellbeing.domain.repositories.EnvironmentalReadingRepository;
import com.performily.flowboard.wellbeing.domain.repositories.MetricThresholdRepository;
import com.performily.flowboard.wellbeing.domain.repositories.OfficeRepository;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WellbeingQueryServiceImpl implements WellbeingQueryService {

    private final OfficeRepository officeRepository;
    private final DeviceRepository deviceRepository;
    private final MetricThresholdRepository thresholdRepository;
    private final EnvironmentalReadingRepository readingRepository;
    private final Duration validityWindow;

    public WellbeingQueryServiceImpl(OfficeRepository officeRepository,
                                     DeviceRepository deviceRepository,
                                     MetricThresholdRepository thresholdRepository,
                                     EnvironmentalReadingRepository readingRepository,
                                     @Value("${wellbeing.readings.validity-window:PT1H}") Duration validityWindow) {
        this.officeRepository = officeRepository;
        this.deviceRepository = deviceRepository;
        this.thresholdRepository = thresholdRepository;
        this.readingRepository = readingRepository;
        this.validityWindow = validityWindow;
    }

    // US47 / US50 - list of offices with their current indicator
    @Override
    public List<OfficeStatusView> getAllOfficeStatuses() {
        return officeRepository.findAll().stream().map(this::buildStatus).toList();
    }

    // US50 - environmental status of one office
    @Override
    public Optional<OfficeStatusView> handle(GetOfficeStatusQuery query) {
        return officeRepository.findById(query.officeId()).map(this::buildStatus);
    }

    private OfficeStatusView buildStatus(Office office) {
        var now = LocalDateTime.now();
        var thresholds = thresholdRepository.findAllByOfficeId(office.getId()).stream()
                .collect(Collectors.toMap(MetricThreshold::getMetricType, t -> t));

        var metrics = new ArrayList<MetricStatusView>();
        LocalDateTime lastReadingAt = null;
        for (MetricType metricType : MetricType.values()) {
            var latest = readingRepository.findLatestByOfficeIdAndMetricType(office.getId(), metricType);
            var threshold = thresholds.get(metricType);
            var optimal = threshold == null ? null : threshold.rangeOf(HealthIndicator.OPTIMAL).orElse(null);

            if (latest.isEmpty()) {
                metrics.add(new MetricStatusView(metricType, null, null, false, null, optimal));
                continue;
            }
            var reading = latest.get();
            var recent = reading.isRecent(validityWindow, now);
            metrics.add(new MetricStatusView(
                    metricType,
                    reading.getMeasurement().value(),
                    reading.getRecordedAt(),
                    recent,
                    recent ? reading.getHealthIndicator() : null,
                    optimal));
            if (lastReadingAt == null || reading.getRecordedAt().isAfter(lastReadingAt)) {
                lastReadingAt = reading.getRecordedAt();
            }
        }

        var upToDate = metrics.stream().anyMatch(MetricStatusView::upToDate);
        var overall = HealthIndicator.worstOf(metrics.stream().map(MetricStatusView::indicator).toList());

        var devices = deviceRepository.findAllByOfficeId(office.getId()).stream()
                .map(d -> new DeviceView(d, readingRepository.findLatestByDeviceId(d.getId())
                        .map(EnvironmentalReading::getRecordedAt).orElse(null)))
                .toList();

        return new OfficeStatusView(office, upToDate, overall, lastReadingAt, metrics, devices);
    }

    // US51 - history of one metric in a date range
    @Override
    public Result<ReadingHistoryView, ApplicationError> handle(GetReadingHistoryQuery q) {
        if (q.metricType() == null || q.from() == null || q.to() == null) {
            return Result.failure(ApplicationError.validationError("history", "metricType, from and to are required"));
        }
        if (q.from().isAfter(q.to())) {
            return Result.failure(ApplicationError.validationError("dateRange", "The start date cannot be after the end date"));
        }
        if (q.to().isAfter(LocalDate.now())) {
            return Result.failure(ApplicationError.validationError("dateRange", "The date range cannot include future dates"));
        }
        if (officeRepository.findById(q.officeId()).isEmpty()) {
            return Result.failure(ApplicationError.notFound("Office", String.valueOf(q.officeId())));
        }

        var readings = readingRepository.findAllByOfficeIdAndMetricTypeBetween(
                q.officeId(), q.metricType(), q.from().atStartOfDay(), q.to().plusDays(1).atStartOfDay().minusNanos(1));

        if (readings.isEmpty()) {
            return Result.success(new ReadingHistoryView(q.officeId(), q.metricType(), q.from(), q.to(),
                    List.of(), List.of(), null, null, null, 0));
        }

        var values = readings.stream().map(r -> r.getMeasurement().value()).toList();
        var minimum = values.stream().min(Comparator.naturalOrder()).orElseThrow();
        var maximum = values.stream().max(Comparator.naturalOrder()).orElseThrow();
        var average = average(values);

        var threshold = thresholdRepository.findByOfficeIdAndMetricType(q.officeId(), q.metricType());
        var byDay = new TreeMap<LocalDate, List<BigDecimal>>();
        readings.forEach(r -> byDay.computeIfAbsent(r.getRecordedAt().toLocalDate(), d -> new ArrayList<>())
                .add(r.getMeasurement().value()));

        var dailyAverages = byDay.entrySet().stream()
                .map(e -> {
                    var dayAverage = average(e.getValue());
                    var indicator = threshold.map(t -> t.classify(dayAverage)).orElse(null);
                    return new ReadingHistoryView.DailyAverage(e.getKey(), dayAverage, indicator);
                })
                .toList();

        var daysAboveAcceptable = (int) dailyAverages.stream()
                .filter(d -> d.indicator() != null && d.indicator().isWorseThan(HealthIndicator.ACCEPTABLE))
                .count();

        return Result.success(new ReadingHistoryView(q.officeId(), q.metricType(), q.from(), q.to(),
                readings, dailyAverages, minimum, maximum, average, daysAboveAcceptable));
    }

    private static BigDecimal average(List<BigDecimal> values) {
        return values.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
    }

    // US48 - devices of the inventory (filter by status, e.g. IN_INVENTORY)
    @Override
    public List<Device> getDevices(DeviceStatus status) {
        return status == null ? deviceRepository.findAll() : deviceRepository.findAllByStatus(status);
    }

    // US49 - thresholds configured for an office
    @Override
    public List<MetricThreshold> getThresholdsByOfficeId(Long officeId) {
        return thresholdRepository.findAllByOfficeId(officeId);
    }
}
