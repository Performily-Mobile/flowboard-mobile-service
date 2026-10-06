package com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.benefits.domain.model.valueobjects.AssignmentStatus;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Persistence entity of a benefit assignment (table benefit_assignments).
 *
 * employee_id and source_area_id are logical references to Workspace, not foreign keys.
 */
@Entity
@Table(name = "benefit_assignments", indexes = {
        @Index(name = "idx_benefit_assignment_employee", columnList = "employee_id"),
        @Index(name = "idx_benefit_assignment_type_employee", columnList = "benefit_type_id, employee_id")
})
public class BenefitAssignmentPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "benefit_type_id", nullable = false)
    private BenefitTypePersistenceEntity benefitType;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "source_area_id")
    private Long sourceAreaId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "quantity", nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private AssignmentStatus status;

    @OneToOne(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private BenefitDeliveryPersistenceEntity delivery;

    public BenefitAssignmentPersistenceEntity() {
    }

    public BenefitTypePersistenceEntity getBenefitType() { return benefitType; }
    public void setBenefitType(BenefitTypePersistenceEntity benefitType) { this.benefitType = benefitType; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getSourceAreaId() { return sourceAreaId; }
    public void setSourceAreaId(Long sourceAreaId) { this.sourceAreaId = sourceAreaId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public AssignmentStatus getStatus() { return status; }
    public void setStatus(AssignmentStatus status) { this.status = status; }

    public BenefitDeliveryPersistenceEntity getDelivery() { return delivery; }
    public void setDelivery(BenefitDeliveryPersistenceEntity delivery) { this.delivery = delivery; }
}
