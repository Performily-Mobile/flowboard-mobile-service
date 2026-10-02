package com.performily.flowboard.request.interfaces.rest.transform;

import com.performily.flowboard.request.domain.model.entities.RequestField;
import com.performily.flowboard.request.domain.model.entities.RequestType;
import com.performily.flowboard.request.interfaces.rest.resources.RequestFieldResource;
import com.performily.flowboard.request.interfaces.rest.resources.RequestTypeResource;

/**
 * Request Type Resource From Entity Assembler
 * @summary
 * Assembler to convert a RequestType entity to a RequestTypeResource.
 *
 * @since 1.0.0
 */
public class RequestTypeResourceFromEntityAssembler {
    /**
     * Converts a {@link RequestType} to a {@link RequestTypeResource}.
     *
     * @param entity the {@link RequestType} instance
     * @return the {@link RequestTypeResource}
     */
    public static RequestTypeResource toResourceFromEntity(RequestType entity) {
        return new RequestTypeResource(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isRequiresAttachment(),
                entity.getBalanceDeduction().name(),
                entity.isActive(),
                entity.getFields().stream().map(RequestTypeResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    private static RequestFieldResource toResourceFromEntity(RequestField field) {
        return new RequestFieldResource(
                field.getId(),
                field.getKey().value(),
                field.getLabel(),
                field.getDataType().name(),
                field.isRequired(),
                field.getDisplayOrder());
    }
}
