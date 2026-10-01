package com.performily.flowboard.workspace.domain.model.entities;

import com.performily.flowboard.shared.domain.model.valueobjects.Money;

import java.util.Objects;

/**
 * Position Entity
 * @summary
 * Represents a job position that belongs to an area and has a reference salary.
 *
 * @since 1.0.0
 */
public class Position {
    private static final int TITLE_MAX_LENGTH = 80;

    private Long id;
    private String title;
    private Area area;
    private Money referenceSalary;
    private boolean active;

    /**
     * Creates a new active position.
     *
     * @param title           the position title
     * @param area            the area the position belongs to
     * @param referenceSalary the reference salary
     */
    public Position(String title, Area area, Money referenceSalary) {
        this(null, title, area, referenceSalary, true);
    }

    /**
     * Rebuilds an existing position. Used by the persistence assemblers.
     *
     * @param id              the position id
     * @param title           the position title
     * @param area            the area the position belongs to
     * @param referenceSalary the reference salary
     * @param active          whether the position is active
     */
    public Position(Long id, String title, Area area, Money referenceSalary, boolean active) {
        this.id = id;
        this.title = validateTitle(title);
        this.area = Objects.requireNonNull(area, "Position area cannot be null");
        this.referenceSalary = Objects.requireNonNull(referenceSalary, "Reference salary cannot be null");
        this.active = active;
    }

    /**
     * Updates the reference salary.
     *
     * @param referenceSalary the new reference salary
     */
    public void updateReferenceSalary(Money referenceSalary) {
        this.referenceSalary = Objects.requireNonNull(referenceSalary, "Reference salary cannot be null");
    }

    /**
     * Checks whether the position belongs to the given area.
     *
     * @param area the area to compare
     * @return true if the position belongs to the area
     */
    public boolean belongsTo(Area area) {
        return area != null && Objects.equals(this.area.getId(), area.getId());
    }

    /**
     * Deactivates the position.
     */
    public void deactivate() {
        if (!this.active) {
            throw new IllegalStateException("Position is already inactive");
        }
        this.active = false;
    }

    private static String validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Position title cannot be null or blank");
        }
        var trimmed = title.trim();
        if (trimmed.length() > TITLE_MAX_LENGTH) {
            throw new IllegalArgumentException("Position title cannot exceed %d characters".formatted(TITLE_MAX_LENGTH));
        }
        return trimmed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Area getArea() {
        return area;
    }

    public Money getReferenceSalary() {
        return referenceSalary;
    }

    public boolean isActive() {
        return active;
    }
}