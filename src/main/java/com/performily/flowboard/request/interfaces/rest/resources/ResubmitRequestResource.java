package com.performily.flowboard.request.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Resubmit Request Resource
 * @summary
 * Resource for sending again a request that was returned for review. The values
 * and attachments replace the previous ones. The period is optional: when it is
 * not sent, the current period is kept.
 *
 * @since 1.0.0
 */
@Schema(name = "ResubmitRequestRequest", description = "Request payload for resubmitting a request")
public record ResubmitRequestResource(
        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "Requester that resubmits the request", example = "3")
        Long actorId,

        @Schema(description = "New start date, or null to keep the current period", example = "2026-10-19")
        LocalDate startDate,

        @Schema(description = "New end date, or null to keep the current period", example = "2026-10-23")
        LocalDate endDate,

        @Schema(description = "New start time, only for permissions of hours", example = "09:00", type = "string")
        LocalTime startTime,

        @Schema(description = "New end time, only for permissions of hours", example = "11:00", type = "string")
        LocalTime endTime,

        @Valid
        @Schema(description = "Values of the dynamic form")
        List<FieldValueResource> fieldValues,

        @Valid
        @Schema(description = "Attachments")
        List<RequestAttachmentResource> attachments,

        @Size(max = 500)
        @Schema(description = "Optional comment for the approver", example = "Adjunté el documento solicitado", maxLength = 500)
        String comment
) {
}
