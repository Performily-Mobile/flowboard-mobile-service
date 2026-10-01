package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.entities.JobAssignment;
import com.performily.flowboard.workspace.interfaces.rest.resources.JobAssignmentResource;

/**
 * Job Assignment Resource From Entity Assembler
 * @summary
 * Assembler to convert a JobAssignment entity to a JobAssignmentResource.
 *
 * @since 1.0.0
 */
public class JobAssignmentResourceFromEntityAssembler {
    /**
     * Converts a {@link JobAssignment} to a {@link JobAssignmentResource}.
     *
     * @param entity the {@link JobAssignment} instance
     * @return the {@link JobAssignmentResource}
     */
    public static JobAssignmentResource toResourceFromEntity(JobAssignment entity) {
        return new JobAssignmentResource(
                entity.getId(),
                entity.getArea().getId(),
                entity.getArea().getName(),
                entity.getPosition().getId(),
                entity.getPosition().getTitle(),
                entity.getChangeType().name(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isCurrent());
    }
}