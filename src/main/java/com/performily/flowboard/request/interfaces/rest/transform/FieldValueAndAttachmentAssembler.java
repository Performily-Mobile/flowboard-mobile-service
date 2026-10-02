package com.performily.flowboard.request.interfaces.rest.transform;

import com.performily.flowboard.request.domain.model.valueobjects.FieldValue;
import com.performily.flowboard.request.interfaces.rest.resources.FieldValueResource;
import com.performily.flowboard.request.interfaces.rest.resources.RequestAttachmentResource;
import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;

import java.util.List;

/**
 * Field Value And Attachment Assembler
 * @summary
 * Converts the form values and the attachments between resources and domain
 * value objects. Shared by the request assemblers.
 *
 * @since 1.0.0
 */
public final class FieldValueAndAttachmentAssembler {
    private FieldValueAndAttachmentAssembler() {
    }

    /**
     * Converts the field value resources to domain values.
     *
     * @param resources the resources, may be null
     * @return the field values
     */
    public static List<FieldValue> toFieldValues(List<FieldValueResource> resources) {
        if (resources == null) return List.of();
        return resources.stream().map(resource -> new FieldValue(resource.key(), resource.value())).toList();
    }

    /**
     * Converts the attachment resources to file references.
     *
     * @param resources the resources, may be null
     * @return the file references
     */
    public static List<FileReference> toFileReferences(List<RequestAttachmentResource> resources) {
        if (resources == null) return List.of();
        return resources.stream()
                .map(resource -> new FileReference(resource.fileName(), resource.contentType(),
                        resource.sizeInBytes() == null ? 0 : resource.sizeInBytes(), resource.storageUrl()))
                .toList();
    }

    /**
     * Converts a domain field value to its resource.
     *
     * @param value the field value
     * @return the resource
     */
    public static FieldValueResource toFieldValueResource(FieldValue value) {
        return new FieldValueResource(value.key().value(), value.value());
    }

    /**
     * Converts a file reference to its resource.
     *
     * @param file the file reference
     * @return the resource
     */
    public static RequestAttachmentResource toAttachmentResource(FileReference file) {
        return new RequestAttachmentResource(file.fileName(), file.contentType(), file.sizeInBytes(), file.storageUrl());
    }
}
