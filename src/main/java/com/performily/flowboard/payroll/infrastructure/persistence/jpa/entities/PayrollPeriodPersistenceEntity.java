package com.performily.flowboard.payroll.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.payroll.infrastructure.persistence.jpa.embeddables.PayPeriodPersistenceEmbeddable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Payroll Period Persistence Entity
 * @summary
 * JPA persistence entity for payroll periods. One row per year and month.
 *
 * It does not extend AuditableAbstractPersistenceEntity because the
 * payroll_periods table already exists without audit columns.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "payroll_periods",
        uniqueConstraints = @UniqueConstraint(columnNames = {"period_year", "period_month"}))
@Getter
@Setter
@NoArgsConstructor
public class PayrollPeriodPersistenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PayPeriodPersistenceEmbeddable period;

    @Column(name = "scheduled_payment_date", nullable = false)
    private LocalDate scheduledPaymentDate;
}
