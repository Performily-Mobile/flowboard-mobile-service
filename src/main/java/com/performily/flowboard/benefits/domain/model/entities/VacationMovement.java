package com.performily.flowboard.benefits.domain.model.entities;

import com.performily.flowboard.benefits.domain.model.valueobjects.VacationMovementType;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.shared.domain.model.valueobjects.RequestId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Vacation Movement Entity
 * @summary
 * One change of a vacation balance. It explains why the balance is what it is.
 *
 * days is the signed effect on the available days: +2.5 for an accrual,
 * -3 for a usage, +3 for a reversal, and + or - for a manual adjustment.
 * requestId is only a reference to Request, never a foreign key.
 *
 * @since 1.0.0
 */
public class VacationMovement {
    private Long id;
    private final VacationMovementType type;
    private final BigDecimal days;
    private final String reason;
    private final EmployeeId authorId;
    private final RequestId requestId;
    private final LocalDateTime occurredAt;

    public VacationMovement(Long id, VacationMovementType type, BigDecimal days, String reason,
                            EmployeeId authorId, RequestId requestId, LocalDateTime occurredAt) {
        if (type == null || days == null || occurredAt == null) {
            throw new IllegalArgumentException("Movement type, days and date are required");
        }
        this.id = id;
        this.type = type;
        this.days = days;
        this.reason = reason;
        this.authorId = authorId;
        this.requestId = requestId;
        this.occurredAt = occurredAt;
    }

    public boolean isUsageOf(RequestId request) {
        return type == VacationMovementType.USAGE && request != null && request.equals(requestId);
    }

    public boolean isReversalOf(RequestId request) {
        return type == VacationMovementType.REVERSAL && request != null && request.equals(requestId);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VacationMovementType getType() { return type; }
    public BigDecimal getDays() { return days; }
    public String getReason() { return reason; }
    public EmployeeId getAuthorId() { return authorId; }
    public RequestId getRequestId() { return requestId; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}
