package com.performily.flowboard.payroll.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.shared.infrastructure.persistence.jpa.embeddables.FileReferencePersistenceEmbeddable;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.embeddables.MoneyPersistenceEmbeddable;
import com.performily.flowboard.payroll.domain.model.valueobjects.PublicationStatus;
import com.performily.flowboard.payroll.infrastructure.persistence.jpa.embeddables.PaymentDetailsPersistenceEmbeddable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Payslip Persistence Entity
 * @summary
 * JPA persistence entity for payslips. One payslip per employee and payroll period.
 *
 * The column names keep the ones of the existing payslips table
 * (file_size, file_url, net_amount) so the stored data can still be read.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "payslips",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "payroll_period_id"}))
@Getter
@Setter
@NoArgsConstructor
public class PayslipPersistenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payroll_period_id", nullable = false)
    private PayrollPeriodPersistenceEntity payrollPeriod;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fileName", column = @Column(name = "file_name", nullable = false)),
            @AttributeOverride(name = "contentType", column = @Column(name = "content_type", length = 100)),
            @AttributeOverride(name = "sizeBytes", column = @Column(name = "file_size")),
            @AttributeOverride(name = "storageUrl", column = @Column(name = "file_url", nullable = false, length = 500))
    })
    private FileReferencePersistenceEmbeddable file;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "net_amount", nullable = false, precision = 10, scale = 2)),
            @AttributeOverride(name = "currency", column = @Column(name = "currency", length = 3))
    })
    private MoneyPersistenceEmbeddable netAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "publication_status", nullable = false, length = 20)
    private PublicationStatus publicationStatus;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Embedded
    private PaymentDetailsPersistenceEmbeddable payment;
}
