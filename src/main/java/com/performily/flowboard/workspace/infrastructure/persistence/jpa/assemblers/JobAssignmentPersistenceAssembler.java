package com.performily.flowboard.workspace.infrastructure.persistence.jpa.assemblers;

import com.performily.flowboard.workspace.domain.model.entities.JobAssignment;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities.EmployeePersistenceEntity;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities.JobAssignmentPersistenceEntity;

/**
 * Job Assignment Persistence Assembler
 * @summary
 * Static assembler between job assignment domain and persistence representations.
 *
 * @since 1.0.0
 */
public final class JobAssignmentPersistenceAssembler {
    private JobAssignmentPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity to its domain representation.
     *
     * @param entity the {@link JobAssignmentPersistenceEntity} instance
     * @return the domain object, or null when the entity is null
     */
    public static JobAssignment toDomainFromPersistence(JobAssignmentPersistenceEntity entity) {
        if (entity == null) return null;
        return new JobAssignment(
                entity.getId(),
                AreaPersistenceAssembler.toDomainFromPersistence(entity.getArea()),
                PositionPersistenceAssembler.toDomainFromPersistence(entity.getPosition()),
                entity.getChangeType(),
                entity.getStartDate(),
                entity.getEndDate());
    }

    /**
     * Converts a domain object to its persistence representation.
     *
     * @param jobAssignment the {@link JobAssignment} instance
     * @param employee the {@link EmployeePersistenceEntity} instance
     * @return the persistence entity, or null when the domain object is null
     */
    public static JobAssignmentPersistenceEntity toPersistenceFromDomain(JobAssignment jobAssignment,
                                                                         EmployeePersistenceEntity employee) {
        if (jobAssignment == null) return null;
        var entity = new JobAssignmentPersistenceEntity();
        if (jobAssignment.getId() != null) {
            entity.setId(jobAssignment.getId());
        }
        entity.setEmployee(employee);
        entity.setArea(AreaPersistenceAssembler.toPersistenceFromDomain(jobAssignment.getArea()));
        entity.setPosition(PositionPersistenceAssembler.toPersistenceFromDomain(jobAssignment.getPosition()));
        entity.setChangeType(jobAssignment.getChangeType());
        entity.setStartDate(jobAssignment.getStartDate());
        entity.setEndDate(jobAssignment.getEndDate());
        return entity;
    }
}