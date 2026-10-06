package com.performily.flowboard.payroll.infrastructure.persistence.jpa.embeddables;

import com.performily.flowboard.payroll.domain.model.valueobjects.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Payment Details Persistence Embeddable
 * @summary
 * JPA embeddable for the PaymentDetails value object.
 *
 * @since 1.0.0
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailsPersistenceEmbeddable {
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "paid_on")
    private LocalDate paidOn;

    @Column(name = "observation_reason", length = 500)
    private String observationReason;
}
