package com.performily.flowboard.workspace.domain.model.entities;

/**
 * Area Entity
 * @summary
 * Represents an organizational area of the company. The name is unique.
 * An area with ACTIVE employees cannot be deactivated; that rule is checked by
 * the application service because it needs the employee repository.
 *
 * @since 1.0.0
 */
public class Area {
    private static final int NAME_MAX_LENGTH = 80;
    private static final int DESCRIPTION_MAX_LENGTH = 255;

    private Long id;
    private String name;
    private String description;
    private boolean active;

    /**
     * Creates a new active area.
     *
     * @param name        the area name
     * @param description the area description, optional
     */
    public Area(String name, String description) {
        this(null, name, description, true);
    }

    /**
     * Rebuilds an existing area. Used by the persistence assemblers.
     *
     * @param id          the area id
     * @param name        the area name
     * @param description the area description
     * @param active      whether the area is active
     */
    public Area(Long id, String name, String description, boolean active) {
        this.id = id;
        this.name = validateName(name);
        this.description = validateDescription(description);
        this.active = active;
    }

    /**
     * Renames the area.
     *
     * @param name the new name
     */
    public void rename(String name) {
        this.name = validateName(name);
    }

    /**
     * Deactivates the area.
     */
    public void deactivate() {
        if (!this.active) {
            throw new IllegalStateException("Area is already inactive");
        }
        this.active = false;
    }

    /**
     * Activates the area.
     */
    public void activate() {
        if (this.active) {
            throw new IllegalStateException("Area is already active");
        }
        this.active = true;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Area name cannot be null or blank");
        }
        var trimmed = name.trim();
        if (trimmed.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Area name cannot exceed %d characters".formatted(NAME_MAX_LENGTH));
        }
        return trimmed;
    }

    private static String validateDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        var trimmed = description.trim();
        if (trimmed.length() > DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("Area description cannot exceed %d characters".formatted(DESCRIPTION_MAX_LENGTH));
        }
        return trimmed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }
}