package com.performily.flowboard.wellbeing.domain.model.valueobjects;

/**
 * Physical location of a workspace.
 *
 * @param address   street address or site, required
 * @param floor     floor or level, required
 * @param reference optional reference, max 150 characters
 */
public record OfficeLocation(String address, String floor, String reference) {
    public OfficeLocation {
        if (address == null || address.isBlank())
            throw new IllegalArgumentException("Office address is required");
        if (floor == null || floor.isBlank())
            throw new IllegalArgumentException("Office floor is required");
        if (reference != null && reference.length() > 150)
            throw new IllegalArgumentException("Office reference cannot exceed 150 characters");
        address = address.trim();
        floor = floor.trim();
        reference = reference == null || reference.isBlank() ? null : reference.trim();
    }
}
