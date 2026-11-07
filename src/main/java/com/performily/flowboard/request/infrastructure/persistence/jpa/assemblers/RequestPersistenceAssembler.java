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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Request Persistence Assembler
 * @summary
 * Static assembler between request domain and persistence representations,
 * including the form values, the attachments and the history.
 *
 * A new request is converted with {@link #toPersistenceFromDomain}. An existing
 * request is copied over its stored entity with {@link #updatePersistenceFromDomain},
 * so Hibernate only writes what changed: the attachments that did not change keep
 * their id and upload date, and only the new history entries are inserted.
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
     * Converts a new domain object (without id) to its persistence representation.
     *
     * @param request     the {@link Request} instance
     * @param requestType the persistence entity of its request type, already managed by JPA
     * @return the persistence entity, or null when the domain object is null
     */
    public static RequestPersistenceEntity toPersistenceFromDomain(Request request, RequestTypePersistenceEntity requestType) {
        if (request == null) return null;
        var entity = new RequestPersistenceEntity();
        entity.setRequesterId(request.getRequesterId().value());
        entity.setRequestType(requestType);
        entity.setSubmittedAt(request.getSubmittedAt());
        updatePersistenceFromDomain(request, entity);
        return entity;
    }

    /**
     * Copies the state of a domain object over its persistence entity.
     *
     * Requester, request type and submission date never change after the
     * submission, so they are not copied. For the collections:
     * - form values: the keys that are no longer present are removed and only the
     *   values that changed are put;
     * - attachments: the files that did not change are kept, the removed ones are
     *   deleted and only the new ones are inserted;
     * - history: it only grows, so only the entries without id are added.
     *
     * @param request the {@link Request} instance
     * @param entity  the {@link RequestPersistenceEntity} to update
     */
    public static void updatePersistenceFromDomain(Request request, RequestPersistenceEntity entity) {
        entity.setStatus(request.getStatus());

        var period = request.getPeriod();
        entity.setStartDate(period == null ? null : period.startDate());
        entity.setEndDate(period == null ? null : period.endDate());
        entity.setStartTime(period == null ? null : period.startTime());
        entity.setEndTime(period == null ? null : period.endTime());

        var approver = request.getApprover();
        entity.setApproverType(approver.type());
        entity.setApproverEmployeeId(approver.employeeId() == null ? null : approver.employeeId().value());

        updateFieldValues(request.getFieldValues(), entity.getFieldValues());
        updateAttachments(request.getAttachments(), entity);
        addNewHistoryEntries(request.getHistory(), entity);
    }

    private static void updateFieldValues(List<FieldValue> values, Map<String, String> stored) {
        var wanted = values.stream()
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toMap(value -> value.key().value(), FieldValue::value,
                        (first, second) -> second, LinkedHashMap::new));
        stored.keySet().retainAll(wanted.keySet());
        wanted.forEach((key, value) -> {
            if (!value.equals(stored.get(key))) {
                stored.put(key, value);
            }
        });
    }

    private static void updateAttachments(List<FileReference> files, RequestPersistenceEntity entity) {
        var pending = new ArrayList<>(files);
        entity.getAttachments().removeIf(attachment -> !pending.remove(toDomainFromPersistence(attachment.getFile())));
        var now = LocalDateTime.now();
        pending.forEach(file -> entity.getAttachments().add(toPersistenceFromDomain(file, entity, now)));
    }

    private static void addNewHistoryEntries(List<RequestHistory> history, RequestPersistenceEntity entity) {
        history.stream()
                .filter(entry -> entry.getId() == null)
                .map(entry -> toPersistenceFromDomain(entry, entity))
                .forEach(entity.getHistory()::add);
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
        entity.setRequest(request);
        entity.setPreviousStatus(history.getPreviousStatus());
        entity.setNewStatus(history.getNewStatus());
        entity.setActorId(history.getActorId() == null ? null : history.getActorId().value());
        entity.setComment(history.getComment());
        entity.setOccurredAt(history.getOccurredAt());
        return entity;
    }
}
