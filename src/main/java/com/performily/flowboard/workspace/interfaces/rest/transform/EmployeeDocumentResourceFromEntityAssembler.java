package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.entities.EmployeeDocument;
import com.performily.flowboard.workspace.interfaces.rest.resources.EmployeeDocumentResource;

/**
 * Employee Document Resource From Entity Assembler
 * @summary
 * Assembler to convert an EmployeeDocument entity to an EmployeeDocumentResource.
 *
 * @since 1.0.0
 */
public class EmployeeDocumentResourceFromEntityAssembler {
    /**
     * Converts a {@link EmployeeDocument} to a {@link EmployeeDocumentResource}.
     *
     * @param entity the {@link EmployeeDocument} instance
     * @return the {@link EmployeeDocumentResource}
     */
    public static EmployeeDocumentResource toResourceFromEntity(EmployeeDocument entity) {
        var file = entity.getFile();
        return new EmployeeDocumentResource(
                entity.getId(),
                entity.getDocumentType().name(),
                file.fileName(),
                file.contentType(),
                file.sizeInBytes(),
                file.storageUrl(),
                entity.getUploadedAt());
    }
}