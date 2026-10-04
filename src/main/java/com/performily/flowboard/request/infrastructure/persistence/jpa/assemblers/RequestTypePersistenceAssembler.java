package com.performily.flowboard.request.infrastructure.persistence.jpa.assemblers;

import com.performily.flowboard.request.domain.model.entities.RequestField;
import com.performily.flowboard.request.domain.model.entities.RequestType;
import com.performily.flowboard.request.domain.model.valueobjects.FieldKey;
import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestFieldPersistenceEntity;
import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestTypePersistenceEntity;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Request Type Persistence Assembler
 * @summary
 * Static assembler between request type domain and persistence representations,
 * including the fields of the form.
 *
 * @since 1.0.0
 */
public final class RequestTypePersistenceAssembler {
    private RequestTypePersistenceAssembler() {
    }

    /**
     * Converts a persistence entity to its domain representation.
     *
     * @param entity the {@link RequestTypePersistenceEntity} instance
     * @return the domain object, or null when the entity is null
     */
    public static RequestType toDomainFromPersistence(RequestTypePersistenceEntity entity) {
        if (entity == null) return null;
        return new RequestType(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isRequiresAttachment(),
                entity.getBalanceDeduction(),
                entity.isActive(),
                entity.getFields().stream().map(RequestTypePersistenceAssembler::toDomainFromPersistence).toList());
    }

    /**
     * Converts a domain object to its persistence representation.
     *
     * @param requestType the {@link RequestType} instance
     * @return the persistence entity, or null when the domain object is null
     */
    public static RequestTypePersistenceEntity toPersistenceFromDomain(RequestType requestType) {
        if (requestType == null) return null;
        var entity = new RequestTypePersistenceEntity();
        if (requestType.getId() != null) {
            entity.setId(requestType.getId());
        }
        entity.setName(requestType.getName());
        entity.setDescription(requestType.getDescription());
        entity.setRequiresAttachment(requestType.isRequiresAttachment());
        entity.setBalanceDeduction(requestType.getBalanceDeduction());
        entity.setActive(requestType.isActive());
        entity.setFields(requestType.getFields().stream()
                .map(field -> toPersistenceFromDomain(field, entity))
                .collect(Collectors.toCollection(ArrayList::new)));
        return entity;
    }

    private static RequestField toDomainFromPersistence(RequestFieldPersistenceEntity entity) {
        return new RequestField(
                entity.getId(),
                new FieldKey(entity.getFieldKey()),
                entity.getLabel(),
                entity.getDataType(),
                entity.isRequired(),
                entity.getDisplayOrder());
    }

    private static RequestFieldPersistenceEntity toPersistenceFromDomain(RequestField field,
                                                                         RequestTypePersistenceEntity requestType) {
        var entity = new RequestFieldPersistenceEntity();
        if (field.getId() != null) {
            entity.setId(field.getId());
        }
        entity.setRequestType(requestType);
        entity.setFieldKey(field.getKey().value());
        entity.setLabel(field.getLabel());
        entity.setDataType(field.getDataType());
        entity.setRequired(field.isRequired());
        entity.setDisplayOrder(field.getDisplayOrder());
        return entity;
    }
}
