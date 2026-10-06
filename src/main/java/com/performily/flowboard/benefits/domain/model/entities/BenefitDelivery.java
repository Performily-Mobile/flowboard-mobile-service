package com.performily.flowboard.benefits.domain.model.entities;

import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;

import java.time.LocalDate;

/**
 * Benefit Delivery Entity
 * @summary
 * Proof that an assigned benefit was effectively given to the employee.
 * It belongs to a BenefitAssignment, which allows only one delivery.
 *
 * @since 1.0.0
 */
public class BenefitDelivery {
    private Long id;
    private final LocalDate deliveredOn;
    private final EmployeeId registeredBy;
    private final String notes;

    /**
     * @param id           the identifier, null when it is new
     * @param deliveredOn  the delivery date
     * @param registeredBy the HR employee that registered it, optional until IAM exists
     * @param notes        optional notes, max 250 characters
     */
    public BenefitDelivery(Long id, LocalDate deliveredOn, EmployeeId registeredBy, String notes) {
        if (deliveredOn == null) {
            throw new IllegalArgumentException("Delivery date is required");
        }
        if (notes != null && notes.trim().length() > 250) {
            throw new IllegalArgumentException("Delivery notes cannot exceed 250 characters");
        }
        this.id = id;
        this.deliveredOn = deliveredOn;
        this.registeredBy = registeredBy;
        this.notes = notes == null || notes.isBlank() ? null : notes.trim();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDeliveredOn() { return deliveredOn; }
    public EmployeeId getRegisteredBy() { return registeredBy; }
    public String getNotes() { return notes; }
}
