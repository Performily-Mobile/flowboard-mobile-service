package com.performily.flowboard.wellbeing.domain.model.queries;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import java.time.LocalDate;

public record GetReadingHistoryQuery(Long officeId, MetricType metricType, LocalDate from, LocalDate to) {}
