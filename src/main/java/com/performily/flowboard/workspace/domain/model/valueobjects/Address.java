package com.performily.flowboard.workspace.domain.model.valueobjects;

/**
 * Address Value Object
 * @summary
 * Postal address of the employee.
 *
 * @param street     the street and number
 * @param district   the district
 * @param province   the province
 * @param department the department
 * @since 1.0.0
 */
public record Address(String street, String district, String province, String department) {
    /**
     * Compact constructor for Address.
     *
     * @throws IllegalArgumentException if a field is blank
     */
    public Address {
        street = requireText(street, "Street");
        district = requireText(district, "District");
        province = requireText(province, "Province");
        department = requireText(department, "Department");
    }

    /**
     * Gets the full address in a single line.
     *
     * @return the formatted address
     */
    public String getFullAddress() {
        return "%s, %s, %s, %s".formatted(street, district, province, department);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("%s cannot be null or blank".formatted(field));
        }
        return value.trim();
    }
}