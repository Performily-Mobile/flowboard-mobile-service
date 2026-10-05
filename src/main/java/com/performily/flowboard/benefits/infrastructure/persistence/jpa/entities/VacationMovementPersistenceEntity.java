package com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.benefits.domain.model.valueobjects.VacationMovementType;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Persistence entity of a vacation movement (table vacation_movements).
 * request_id is only a reference to Request, never a foreign key between contexts.
 */
@Entity
@Table(name = "vacation_movements", indexes = @Index(name = "idx_vacation_movement_request", columnList = "request_id"))
public class VacationMovementPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "vacation_balance_id", nullable = false)
    private VacationBalancePersistenceEntity vacationBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private VacationMovementType type;

    @Column(name = "days", nullable = false, precision = 6, scale = 2)
    private BigDecimal days;

    @Column(name = "reason", length = 250)
    private String reason;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    public VacationMovementPersistenceEntity() {
    }

    public VacationBalancePersistenceEntity getVacationBalance() { return vacationBalance; }
    public void setVacationBalance(VacationBalancePersistenceEntity vacationBalance) { this.vacationBalance = vacationBalance; }

    public VacationMovementType getType() { return type; }
    public void setType(VacationMovementType type) { this.type = type; }

    public BigDecimal getDays() { return days; }
    public void setDays(BigDecimal days) { this.days = days; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
