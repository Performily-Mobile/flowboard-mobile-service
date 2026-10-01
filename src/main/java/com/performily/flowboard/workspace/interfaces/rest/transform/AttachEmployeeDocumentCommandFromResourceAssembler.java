package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.AttachEmployeeDocumentCommand;
import com.performily.flowboard.workspace.domain.model.valueobjects.DocumentType;
import com.performily.flowboard.workspace.interfaces.rest.resources.AttachEmployeeDocumentResource;

import java.util.Locale;

/**
 * Attach Employee Document Command From Resource Assembler
 * @summary
 * Assembler to convert an AttachEmployeeDocumentResource to an AttachEmployeeDocumentCommand.
 *
 * @since 1.0.0
 */
public class AttachEmployeeDocumentCommandFromResourceAssembler {
    /**
     * Converts a {@link AttachEmployeeDocumentResource} to a {@link AttachEmployeeDocumentCommand}.
     *
     * @param employeeId the employee id
     * @param resource the {@link AttachEmployeeDocumentResource} instance
     * @return the {@link AttachEmployeeDocumentCommand}
     */
    public static AttachEmployeeDocumentCommand toCommandFromResource(Long employeeId, AttachEmployeeDocumentResource resource) {
        return new AttachEmployeeDocumentCommand(
                employeeId,
                DocumentType.valueOf(resource.documentType().trim().toUpperCase(Locale.ROOT)),
                resource.fileName(),
                resource.contentType(),
                resource.sizeInBytes(),
                resource.storageUrl());
    }
}