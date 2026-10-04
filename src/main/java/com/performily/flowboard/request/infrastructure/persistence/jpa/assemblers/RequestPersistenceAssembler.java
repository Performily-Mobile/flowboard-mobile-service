package com.performily.flowboard.request.infrastructure.persistence.jpa.assemblers;

import com.performily.flowboard.request.domain.model.aggregates.Request;
import com.performily.flowboard.request.domain.model.entities.RequestHistory;
import com.performily.flowboard.request.domain.model.valueobjects.Approver;
import com.performily.flowboard.request.domain.model.valueobjects.FieldValue;
import com.performily.flowboard.request.domain.model.valueobjects.RequestPeriod;
import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestAttachmentPersistenceEntity;
import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestHistoryPersistenceEntity;
import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestPersistenceEntity;
import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestTypePersistenceEntity;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.embeddables.FileReferencePersistenceEmbeddable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * Request Persistence Assembler
 * @summary
 * Static assembler between request domain and persistence representations,
 * including the form values, the attachments and the history.
 *
 * @since 1.0.0
 */
public final class RequestPersistenceAssembler {
    private RequestPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity to its domain representation.
     *
     * @param entity the {@link RequestPersistenceEntity} instance
     * @return the domain object, or null when the entity is null
     */
    public static Request toDomainFromPersistence(RequestPersistenceEntity entity) {
        if (entity == null) return null;
        var period = entity.getStartDate() == null
                ? null
                : new RequestPeriod(entity.getStartDate(), entity.getEndDate(), entity.getStartTime(), entity.getEndTime());
        var approver = new Approver(entity.getApproverType(),
                entity.getApproverEmployeeId() == null ? null : new EmployeeId(entity.getApproverEmployeeId()));
        return new Request(
                entity.getId(),
                new EmployeeId(entity.getRequesterId()),
                RequestTypePersistenceAssembler.toDomainFromPersistence(entity.getRequestType()),
                period,
                entity.getFieldValues().entrySet().stream()
                        .map(value -> new FieldValue(value.getKey(), value.getValue()))
                        .toList(),
                entity.getAttachments().stream()
                        .map(attachment -> toDomainFromPersistence(attachment.getFile()))
                        .toList(),
                approver,
                entity.getStatus(),
                entity.getSubmittedAt(),
                entity.getHistory().stream()
                        .map(RequestPersistenceAssembler::toDomainFromPersistence)
                        .toList());
    }

    /**
     * Converts a domain object to its persistence representation.
     *
     * @param request     the {@link Request} instance
     * @param requestType the persistence entity of its request type, already managed by JPA
     * @return the persistence entity, or null when the domain object is null
     */
    public static RequestPersistenceEntity toPersistenceFromDomain(Request request, RequestTypePersistenceEntity requestType) {
        if (request == null) return null;
        var entity = new RequestPersistenceEntity();
        if (request.getId() != null) {
            entity.setId(request.getId());
        }
        entity.setRequesterId(request.getRequesterId().value());
        entity.setRequestType(requestType);
        entity.setStatus(request.getStatus());
        var period = request.getPeriod();
        if (period != null) {
            entity.setStartDate(period.startDate());
            entity.setEndDate(period.endDate());
            entity.setStartTime(period.startTime());
            entity.setEndTime(period.endTime());
        }
        entity.setApproverType(request.getApprover().type());
        entity.setApproverEmployeeId(request.getApprover().employeeId() == null
                ? null
                : request.getApprover().employeeId().value());
        entity.setSubmittedAt(request.getSubmittedAt());
        entity.setFieldValues(request.getFieldValues().stream()
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toMap(value -> value.key().value(), FieldValue::value,
                        (first, second) -> second, LinkedHashMap::new)));
        var now = LocalDateTime.now();
        entity.setAttachments(request.getAttachments().stream()
                .map(file -> toPersistenceFromDomain(file, entity, now))
                .collect(Collectors.toCollection(ArrayList::new)));
        entity.setHistory(request.getHistory().stream()
                .map(history -> toPersistenceFromDomain(history, entity))
                .collect(Collectors.toCollection(ArrayList::new)));
        return entity;
    }

    private static FileReference toDomainFromPersistence(FileReferencePersistenceEmbeddable file) {
        return new FileReference(file.getFileName(), file.getContentType(), file.getSizeBytes(), file.getStorageUrl());
    }

    private static RequestAttachmentPersistenceEntity toPersistenceFromDomain(FileReference file,
                                                                              RequestPersistenceEntity request,
                                                                              LocalDateTime uploadedAt) {
        var entity = new RequestAttachmentPersistenceEntity();
        entity.setRequest(request);
        entity.setFile(new FileReferencePersistenceEmbeddable(
                file.fileName(), file.contentType(), file.sizeInBytes(), file.storageUrl()));
        entity.setUploadedAt(uploadedAt);
        return entity;
    }

    private static RequestHistory toDomainFromPersistence(RequestHistoryPersistenceEntity entity) {
        return new RequestHistory(
                entity.getId(),
                entity.getPreviousStatus(),
                entity.getNewStatus(),
                entity.getActorId() == null ? null : new EmployeeId(entity.getActorId()),
                entity.getComment(),
                entity.getOccurredAt());
    }

    private static RequestHistoryPersistenceEntity toPersistenceFromDomain(RequestHistory history,
                                                                          RequestPersistenceEntity request) {
        var entity = new RequestHistoryPersistenceEntity();
        if (history.getId() != null) {
            entity.setId(history.getId());
        }
        entity.setRequest(request);
        entity.setPreviousStatus(history.getPreviousStatus());
        entity.setNewStatus(history.getNewStatus());
        entity.setActorId(history.getActorId() == null ? null : history.getActorId().value());
        entity.setComment(history.getComment());
        entity.setOccurredAt(history.getOccurredAt());
        return entity;
    }
}
