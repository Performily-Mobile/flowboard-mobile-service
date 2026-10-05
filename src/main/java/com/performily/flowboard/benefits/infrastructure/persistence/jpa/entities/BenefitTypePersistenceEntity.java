package com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.benefits.domain.model.valueobjects.BenefitUnit;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

/**
 * Persistence entity of the benefit catalog (table benefit_types).
 */
@Entity
@Table(name = "benefit_types")
public class BenefitTypePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "name", nullable = false, unique = true, length = 80)
    private String name;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "has_balance", nullable = false)
    private boolean hasBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false, length = 10)
    private BenefitUnit unit;

    @Column(name = "active", nullable = false)
    private boolean active;

    public BenefitTypePersistenceEntity() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isHasBalance() { return hasBalance; }
    public void setHasBalance(boolean hasBalance) { this.hasBalance = hasBalance; }

    public BenefitUnit getUnit() { return unit; }
    public void setUnit(BenefitUnit unit) { this.unit = unit; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
