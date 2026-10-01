package com.performily.flowboard.workspace.domain.model.commands;

import com.performily.flowboard.workspace.domain.model.valueobjects.DocumentType;

/**
 * Attach Employee Document Command
 * @summary
 * Command to attach a document (PDF, JPG or PNG) to an employee record.
 *
 * @since 1.0.0
 */
public record AttachEmployeeDocumentCommand(
        Long employeeId,
        DocumentType documentType,
        String fileName,
        String contentType,
        Long sizeInBytes,
        String storageUrl) {
    /**
     * Compact constructor for AttachEmployeeDocumentCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public AttachEmployeeDocumentCommand {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
        if (documentType == null) {
            throw new IllegalArgumentException("documentType cannot be null");
        }
        if (sizeInBytes == null) {
            throw new IllegalArgumentException("sizeInBytes cannot be null");
        }
    }
}