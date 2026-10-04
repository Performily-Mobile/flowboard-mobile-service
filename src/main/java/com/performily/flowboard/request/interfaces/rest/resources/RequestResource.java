package com.performily.flowboard.request.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Request Resource
 * @summary
 * Resource for a request with its form values, attachments and history.
 *
 * @since 1.0.0
 */
@Schema(name = "RequestResponse", description = "Request information response")
public record RequestResource(
        @Schema(description = "Request identifier", example = "1") Long id,
        @Schema(description = "Requester employee id", example = "3") Long requesterId,
        @Schema(description = "Request type identifier", example = "1") Long requestTypeId,
        @Schema(description = "Request type name", example = "Vacaciones") String requestTypeName,
        @Schema(description = "Status", example = "IN_PROGRESS") String status,
        @Schema(description = "Start date", example = "2026-10-12") LocalDate startDate,
        @Schema(description = "End date", example = "2026-10-16") LocalDate endDate,
        @Schema(description = "Start time", example = "09:00", type = "string") LocalTime startTime,
        @Schema(description = "End time", example = "11:00", type = "string") LocalTime endTime,
        @Schema(description = "Requested calendar days", example = "5") int requestedDays,
        @Schema(description = "Approver type", example = "DIRECT_MANAGER") String approverType,
        @Schema(description = "Approver employee id, null for HR_STAFF", example = "2") Long approverEmployeeId,
        @Schema(description = "Submission date and time") LocalDateTime submittedAt,
        @Schema(description = "Values of the dynamic form") List<FieldValueResource> fieldValues,
        @Schema(description = "Attachments") List<RequestAttachmentResource> attachments,
        @Schema(description = "History of status changes, oldest first") List<RequestHistoryResource> history
) {
}
