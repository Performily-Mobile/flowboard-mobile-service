package com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Persistence entity of a benefit delivery (table benefit_deliveries).
 * The unique assignment_id makes the database also reject a second delivery.
 */
@Entity
@Table(name = "benefit_deliveries")
public class BenefitDeliveryPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @OneToOne(optional = false)
    @JoinColumn(name = "assignment_id", nullable = false, unique = true)
    private BenefitAssignmentPersistenceEntity assignment;

    @Column(name = "delivered_on", nullable = false)
    private LocalDate deliveredOn;

    @Column(name = "registered_by")
    private Long registeredBy;

    @Column(name = "notes", length = 250)
    private String notes;

    public BenefitDeliveryPersistenceEntity() {
    }

    public BenefitAssignmentPersistenceEntity getAssignment() { return assignment; }
    public void setAssignment(BenefitAssignmentPersistenceEntity assignment) { this.assignment = assignment; }

    public LocalDate getDeliveredOn() { return deliveredOn; }
    public void setDeliveredOn(LocalDate deliveredOn) { this.deliveredOn = deliveredOn; }

    public Long getRegisteredBy() { return registeredBy; }
    public void setRegisteredBy(Long registeredBy) { this.registeredBy = registeredBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
