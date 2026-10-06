package com.performily.flowboard.benefits.infrastructure.persistence.jpa.assemblers;

import com.performily.flowboard.benefits.domain.model.aggregates.BenefitAssignment;
import com.performily.flowboard.benefits.domain.model.entities.BenefitDelivery;
import com.performily.flowboard.benefits.domain.model.valueobjects.BenefitQuantity;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities.BenefitAssignmentPersistenceEntity;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities.BenefitDeliveryPersistenceEntity;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities.BenefitTypePersistenceEntity;
import com.performily.flowboard.shared.domain.model.valueobjects.AreaId;
import com.performily.flowboard.shared.domain.model.valueobjects.DateRange;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;

/**
 * Converts between the BenefitAssignment aggregate (with its delivery) and its persistence entities.
 */
public final class BenefitAssignmentPersistenceAssembler {
    private BenefitAssignmentPersistenceAssembler() {
    }

    public static BenefitAssignment toDomainFromPersistence(BenefitAssignmentPersistenceEntity entity) {
        var delivery = entity.getDelivery() == null ? null : new BenefitDelivery(
                entity.getDelivery().getId(),
                entity.getDelivery().getDeliveredOn(),
                entity.getDelivery().getRegisteredBy() == null ? null : new EmployeeId(entity.getDelivery().getRegisteredBy()),
                entity.getDelivery().getNotes());
        return new BenefitAssignment(
                entity.getId(),
                BenefitTypePersistenceAssembler.toDomainFromPersistence(entity.getBenefitType()),
                new EmployeeId(entity.getEmployeeId()),
                entity.getSourceAreaId() == null ? null : new AreaId(entity.getSourceAreaId()),
                new DateRange(entity.getStartDate(), entity.getEndDate()),
                new BenefitQuantity(entity.getQuantity()),
                entity.getStatus(),
                delivery);
    }

    public static BenefitAssignmentPersistenceEntity toPersistenceFromDomain(BenefitAssignment assignment,
                                                                             BenefitTypePersistenceEntity benefitType) {
        var entity = new BenefitAssignmentPersistenceEntity();
        entity.setId(assignment.getId());
        entity.setBenefitType(benefitType);
        entity.setEmployeeId(assignment.getEmployeeId().value());
        entity.setSourceAreaId(assignment.getSourceAreaId() == null ? null : assignment.getSourceAreaId().value());
        entity.setStartDate(assignment.getValidity().startDate());
        entity.setEndDate(assignment.getValidity().endDate());
        entity.setQuantity(assignment.getQuantity().value());
        entity.setStatus(assignment.getStatus());
        if (assignment.getDelivery() != null) {
            var delivery = new BenefitDeliveryPersistenceEntity();
            delivery.setId(assignment.getDelivery().getId());
            delivery.setAssignment(entity);
            delivery.setDeliveredOn(assignment.getDelivery().getDeliveredOn());
            delivery.setRegisteredBy(assignment.getDelivery().getRegisteredBy() == null
                    ? null : assignment.getDelivery().getRegisteredBy().value());
            delivery.setNotes(assignment.getDelivery().getNotes());
            entity.setDelivery(delivery);
        }
        return entity;
    }
}
