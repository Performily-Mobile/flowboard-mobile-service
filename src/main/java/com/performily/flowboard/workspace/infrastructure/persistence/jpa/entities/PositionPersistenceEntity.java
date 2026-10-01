package com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.shared.infrastructure.persistence.jpa.embeddables.MoneyPersistenceEmbeddable;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Position Persistence Entity
 * @summary
 * JPA persistence entity for positions.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "positions")
@Getter
@Setter
@NoArgsConstructor
public class PositionPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private AreaPersistenceEntity area;

    @Column(nullable = false, length = 80)
    private String title;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "reference_salary_amount", nullable = false, precision = 12, scale = 2)),
            @AttributeOverride(name = "currency", column = @Column(name = "reference_salary_currency", nullable = false, length = 3))})
    private MoneyPersistenceEmbeddable referenceSalary;

    @Column(nullable = false)
    private boolean active;
}