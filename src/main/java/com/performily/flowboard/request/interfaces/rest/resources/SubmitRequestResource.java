package com.performily.flowboard.request.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Submit Request Resource
 * @summary
 * Resource for submitting a request. The period is optional (for example an
 * employment certificate); times are only used for permissions of hours.
 *
 * @since 1.0.0
 */
@Schema(name = "SubmitRequestRequest", description = "Request payload for submitting a request")
public record SubmitRequestResource(
        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "Employee that submits the request", example = "3")
        Long requesterId,

        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "Request type identifier", example = "1")
        Long requestTypeId,

        @Schema(description = "Start date", example = "2026-10-12")
        LocalDate startDate,

        @Schema(description = "End date", example = "2026-10-16")
        LocalDate endDate,

        @Schema(description = "Start time, only for permissions of hours", example = "09:00", type = "string")
        LocalTime startTime,

        @Schema(description = "End time, only for permissions of hours", example = "11:00", type = "string")
        LocalTime endTime,

        @Valid
        @Schema(description = "Values of the dynamic form")
        List<FieldValueResource> fieldValues,

        @Valid
        @Schema(description = "Attachments")
        List<RequestAttachmentResource> attachments
) {
}
