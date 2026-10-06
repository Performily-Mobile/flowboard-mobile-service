package com.performily.flowboard.benefits.domain.model.entities;

import com.performily.flowboard.benefits.domain.model.valueobjects.BenefitUnit;

/**
 * Benefit Type Entity
 * @summary
 * Benefit of the organization catalog (bonus, food vouchers, christmas basket,
 * birthday leave...), with the unit in which it is measured.
 *
 * Rules:
 * - Name is required (max 80) and unique in the catalog (checked by the application service).
 * - Only active types can be assigned. A type is never deleted, it is deactivated,
 *   so the assignments that used it keep their history.
 *
 * @since 1.0.0
 */
public class BenefitType {
    private Long id;
    private String name;
    private String description;
    private boolean hasBalance;
    private BenefitUnit unit;
    private boolean active;

    /**
     * Creates a new active benefit type.
     *
     * @param name        the name, unique in the catalog
     * @param description an optional description
     * @param hasBalance  whether the benefit keeps a balance (e.g. days of birthday leave)
     * @param unit        the unit of measure
     */
    public BenefitType(String name, String description, boolean hasBalance, BenefitUnit unit) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Benefit type name is required");
        }
        if (name.trim().length() > 80) {
            throw new IllegalArgumentException("Benefit type name cannot exceed 80 characters");
        }
        if (description != null && description.trim().length() > 250) {
            throw new IllegalArgumentException("Benefit type description cannot exceed 250 characters");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Benefit unit is required");
        }
        this.name = name.trim();
        this.description = description == null || description.isBlank() ? null : description.trim();
        this.hasBalance = hasBalance;
        this.unit = unit;
        this.active = true;
    }

    /**
     * Rebuilds a stored benefit type.
     */
    public BenefitType(Long id, String name, String description, boolean hasBalance, BenefitUnit unit, boolean active) {
        this(name, description, hasBalance, unit);
        this.id = id;
        this.active = active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean hasBalance() { return hasBalance; }
    public BenefitUnit getUnit() { return unit; }
    public boolean isActive() { return active; }
}
