package com.performily.flowboard.request.interfaces.rest.transform;

import com.performily.flowboard.request.domain.model.aggregates.Request;
import com.performily.flowboard.request.domain.model.entities.RequestHistory;
import com.performily.flowboard.request.interfaces.rest.resources.RequestHistoryResource;
import com.performily.flowboard.request.interfaces.rest.resources.RequestResource;

/**
 * Request Resource From Entity Assembler
 * @summary
 * Assembler to convert a Request aggregate to a RequestResource.
 *
 * @since 1.0.0
 */
public class RequestResourceFromEntityAssembler {
    /**
     * Converts a {@link Request} to a {@link RequestResource}.
     *
     * @param entity the {@link Request} instance
     * @return the {@link RequestResource}
     */
    public static RequestResource toResourceFromEntity(Request entity) {
        var period = entity.getPeriod();
        var approver = entity.getApprover();
        return new RequestResource(
                entity.getId(),
                entity.getRequesterId().value(),
                entity.getRequestType().getId(),
                entity.getRequestType().getName(),
                entity.getStatus().name(),
                period == null ? null : period.startDate(),
                period == null ? null : period.endDate(),
                period == null ? null : period.startTime(),
                period == null ? null : period.endTime(),
                entity.requestedDays(),
                approver.type().name(),
                approver.employeeId() == null ? null : approver.employeeId().value(),
                entity.getSubmittedAt(),
                entity.getFieldValues().stream().map(FieldValueAndAttachmentAssembler::toFieldValueResource).toList(),
                entity.getAttachments().stream().map(FieldValueAndAttachmentAssembler::toAttachmentResource).toList(),
                entity.getHistory().stream().map(RequestResourceFromEntityAssembler::toResource).toList());
    }

    private static RequestHistoryResource toResource(RequestHistory history) {
        return new RequestHistoryResource(
                history.getId(),
                history.getPreviousStatus() == null ? null : history.getPreviousStatus().name(),
                history.getNewStatus().name(),
                history.getActorId() == null ? null : history.getActorId().value(),
                history.getComment(),
                history.getOccurredAt());
    }
}
