package com.performily.flowboard.workspace.infrastructure.persistence.jpa.assemblers;

import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.embeddables.FileReferencePersistenceEmbeddable;
import com.performily.flowboard.workspace.domain.model.entities.EmployeeDocument;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities.EmployeeDocumentPersistenceEntity;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities.EmployeePersistenceEntity;

/**
 * Employee Document Persistence Assembler
 * @summary
 * Static assembler between employee document domain and persistence representations.
 *
 * @since 1.0.0
 */
public final class EmployeeDocumentPersistenceAssembler {
    private EmployeeDocumentPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity to its domain representation.
     *
     * @param entity the {@link EmployeeDocumentPersistenceEntity} instance
     * @return the domain object, or null when the entity is null
     */
    public static EmployeeDocument toDomainFromPersistence(EmployeeDocumentPersistenceEntity entity) {
        if (entity == null) return null;
        var file = entity.getFile();
        return new EmployeeDocument(
                entity.getId(),
                entity.getDocumentType(),
                new FileReference(file.getFileName(), file.getContentType(), file.getSizeBytes(), file.getStorageUrl()),
                entity.getUploadedAt());
    }

    /**
     * Converts a domain object to its persistence representation.
     *
     * @param document the {@link EmployeeDocument} instance
     * @param employee the {@link EmployeePersistenceEntity} instance
     * @return the persistence entity, or null when the domain object is null
     */
    public static EmployeeDocumentPersistenceEntity toPersistenceFromDomain(EmployeeDocument document,
                                                                            EmployeePersistenceEntity employee) {
        if (document == null) return null;
        var entity = new EmployeeDocumentPersistenceEntity();
        if (document.getId() != null) {
            entity.setId(document.getId());
        }
        entity.setEmployee(employee);
        entity.setDocumentType(document.getDocumentType());
        var file = document.getFile();
        entity.setFile(new FileReferencePersistenceEmbeddable(
                file.fileName(), file.contentType(), file.sizeInBytes(), file.storageUrl()));
        entity.setUploadedAt(document.getUploadedAt());
        return entity;
    }
}