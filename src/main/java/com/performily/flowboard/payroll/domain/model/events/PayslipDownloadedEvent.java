package com.performily.flowboard.payroll.domain.model.events;

import java.time.LocalDateTime;

public record PayslipDownloadedEvent(Long payslipId, Long employeeId, LocalDateTime occurredOn) {}