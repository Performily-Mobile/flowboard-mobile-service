package com.performily.flowboard.benefits.domain.model.aggregates;

import com.performily.flowboard.benefits.domain.model.entities.BenefitDelivery;
import com.performily.flowboard.benefits.domain.model.entities.BenefitType;
import com.performily.flowboard.benefits.domain.model.events.BenefitAssignedEvent;
import com.performily.flowboard.benefits.domain.model.events.BenefitDeliveredEvent;
import com.performily.flowboard.benefits.domain.model.valueobjects.AssignmentStatus;
import com.performily.flowboard.benefits.domain.model.valueobjects.BenefitQuantity;
import com.performily.flowboard.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.performily.flowboard.shared.domain.model.valueobjects.AreaId;
import com.performily.flowboard.shared.domain.model.valueobjects.DateRange;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Benefit Assignment Aggregate Root
 * @summary
 * A benefit of the catalog given to one employee for a validity period,
 * with its quantity and, once it happens, its delivery.
 *
 * Invariants checked by the aggregate:
 * - Only active benefit types can be assigned.
 * - The quantity must be valid for the unit of the type (whole numbers for UNITS).
 * - Assigning to an area creates one assignment per employee and keeps sourceAreaId.
 * - A delivery can be registered only once, never on a cancelled assignment,
 *   never in the future and never before the validity starts.
 * - Only ASSIGNED assignments can be cancelled.
 *
 * Rules that need other aggregates or other bounded contexts (the employee is ACTIVE
 * in Workspace, there is no overlapping assignment of the same type) are checked by
 * the application service.
 *
 * Events: BenefitAssignedEvent, BenefitDeliveredEvent.
 *
 * @since 1.0.0
 */
public class BenefitAssignment extends AbstractDomainAggregateRoot<BenefitAssignment> {
    private Long id;
    private final BenefitType benefitType;
    private final EmployeeId employeeId;
    private final AreaId sourceAreaId;
    private final DateRange validity;
    private final BenefitQuantity quantity;
    private AssignmentStatus status;
    private BenefitDelivery delivery;

    /**
     * Assigns a benefit to one employee.
     *
     * @param benefitType the benefit type, it must be active
     * @param employeeId  the employee
     * @param validity    the validity period
     * @param quantity    the quantity, in the unit of the type
     */
    public BenefitAssignment(BenefitType benefitType, EmployeeId employeeId, DateRange validity, BenefitQuantity quantity) {
        this(benefitType, employeeId, null, validity, quantity);
    }

    private BenefitAssignment(BenefitType benefitType, EmployeeId employeeId, AreaId sourceAreaId,
                              DateRange validity, BenefitQuantity quantity) {
        this.benefitType = Objects.requireNonNull(benefitType, "Benefit type is required");
        this.employeeId = Objects.requireNonNull(employeeId, "Employee is required");
        this.validity = Objects.requireNonNull(validity, "Validity period is required");
        this.quantity = Objects.requireNonNull(quantity, "Quantity is required");
        if (!benefitType.isActive()) {
            throw new IllegalArgumentException("Benefit type '%s' is not active".formatted(benefitType.getName()));
        }
        if (!quantity.isCompatibleWith(benefitType.getUnit())) {
            throw new IllegalArgumentException("Quantity %s is not valid for a benefit measured in %s"
                    .formatted(quantity.value().stripTrailingZeros().toPlainString(), benefitType.getUnit()));
        }
        this.sourceAreaId = sourceAreaId;
        this.status = AssignmentStatus.ASSIGNED;
    }

    /**
     * Rebuilds a stored assignment.
     */
    public BenefitAssignment(Long id, BenefitType benefitType, EmployeeId employeeId, AreaId sourceAreaId,
                             DateRange validity, BenefitQuantity quantity, AssignmentStatus status,
                             BenefitDelivery delivery) {
        this.id = id;
        this.benefitType = benefitType;
        this.employeeId = employeeId;
        this.sourceAreaId = sourceAreaId;
        this.validity = validity;
        this.quantity = quantity;
        this.status = status;
        this.delivery = delivery;
    }

    /**
     * Assigns a benefit to the employees of an area: one assignment per employee,
     * all of them keep the area as their source.
     *
     * @param benefitType the benefit type, it must be active
     * @param areaId      the area
     * @param employeeIds the active employees of the area that will receive it
     * @param validity    the validity period
     * @param quantity    the quantity, in the unit of the type
     * @return one assignment per employee
     */
    public static List<BenefitAssignment> forArea(BenefitType benefitType, AreaId areaId, List<EmployeeId> employeeIds,
                                                  DateRange validity, BenefitQuantity quantity) {
        Objects.requireNonNull(areaId, "Area is required");
        if (employeeIds == null || employeeIds.isEmpty()) {
            throw new IllegalArgumentException("The area has no employees to receive the benefit");
        }
        return employeeIds.stream()
                .map(employeeId -> new BenefitAssignment(benefitType, employeeId, areaId, validity, quantity))
                .toList();
    }

    /**
     * Called by the repository once the new assignment has an id, so the event carries it.
     */
    public void onAssigned() {
        registerDomainEvent(new BenefitAssignedEvent(id, employeeId.value(), benefitType.getName(), LocalDateTime.now()));
    }

    /**
     * Registers the effective delivery of the benefit. It can happen only once.
     *
     * @param deliveredOn  the delivery date, not in the future and not before the validity starts
     * @param registeredBy the HR employee that registers it, optional until IAM exists
     * @param notes        optional notes
     */
    public void registerDelivery(LocalDate deliveredOn, EmployeeId registeredBy, String notes) {
        if (status == AssignmentStatus.DELIVERED) {
            throw new IllegalStateException("The benefit was already delivered on %s".formatted(delivery.getDeliveredOn()));
        }
        if (status == AssignmentStatus.CANCELLED) {
            throw new IllegalStateException("A cancelled assignment cannot be delivered");
        }
        if (deliveredOn == null) {
            throw new IllegalArgumentException("Delivery date is required");
        }
        if (deliveredOn.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Delivery date cannot be in the future");
        }
        if (deliveredOn.isBefore(validity.startDate())) {
            throw new IllegalArgumentException("Delivery date cannot be before the validity starts (%s)"
                    .formatted(validity.startDate()));
        }
        this.delivery = new BenefitDelivery(null, deliveredOn, registeredBy, notes);
        this.status = AssignmentStatus.DELIVERED;
        registerDomainEvent(new BenefitDeliveredEvent(id, employeeId.value(), deliveredOn, LocalDateTime.now()));
    }

    /**
     * Cancels the assignment. A delivered benefit cannot be cancelled.
     */
    public void cancel() {
        if (status != AssignmentStatus.ASSIGNED) {
            throw new IllegalStateException("Only assigned benefits can be cancelled, this one is %s".formatted(status));
        }
        this.status = AssignmentStatus.CANCELLED;
    }

    public boolean isDelivered() {
        return status == AssignmentStatus.DELIVERED;
    }

    public boolean isValidOn(LocalDate date) {
        return validity.contains(date);
    }

    /**
     * A current benefit is assigned, not delivered yet, and its validity has not ended.
     *
     * @param today the reference date
     * @return true when the employee still can receive it
     */
    public boolean isCurrentOn(LocalDate today) {
        return status == AssignmentStatus.ASSIGNED && !validity.endDate().isBefore(today);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BenefitType getBenefitType() { return benefitType; }
    public EmployeeId getEmployeeId() { return employeeId; }
    public AreaId getSourceAreaId() { return sourceAreaId; }
    public DateRange getValidity() { return validity; }
    public BenefitQuantity getQuantity() { return quantity; }
    public AssignmentStatus getStatus() { return status; }
    public BenefitDelivery getDelivery() { return delivery; }
}
