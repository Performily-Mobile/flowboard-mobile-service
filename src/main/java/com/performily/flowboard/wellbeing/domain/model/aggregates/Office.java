package com.performily.flowboard.wellbeing.domain.model.aggregates;

import com.performily.flowboard.wellbeing.domain.model.valueobjects.OfficeLocation;

/**
 * Office Aggregate Root.
 * A workspace of the organization whose environmental conditions are measured.
 * Separate Ways with Workspace: it does not reference employees nor areas,
 * the area is a free text label (e.g. "Almacén").
 */
public class Office {
    private Long id;
    private String name;
    private String area;
    private OfficeLocation location;
    private boolean active;

    public Office(String name, String area, OfficeLocation location) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Office name is required");
        if (name.trim().length() > 80)
            throw new IllegalArgumentException("Office name cannot exceed 80 characters");
        if (area != null && area.trim().length() > 60)
            throw new IllegalArgumentException("Office area cannot exceed 60 characters");
        if (location == null)
            throw new IllegalArgumentException("Office location is required");
        this.name = name.trim();
        this.area = area == null || area.isBlank() ? null : area.trim();
        this.location = location;
        this.active = true;
    }

    public Office(Long id, String name, String area, OfficeLocation location, boolean active) {
        this(name, area, location);
        this.id = id;
        this.active = active;
    }

    public void deactivate() {
        this.active = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public String getArea() { return area; }
    public OfficeLocation getLocation() { return location; }
    public boolean isActive() { return active; }
}
