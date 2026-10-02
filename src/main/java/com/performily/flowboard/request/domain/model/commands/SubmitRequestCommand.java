package com.performily.flowboard.request.domain.model.commands;

import com.performily.flowboard.request.domain.model.valueobjects.FieldValue;
import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Submit Request Command
 * @summary
 * Command to submit a new request. The period is optional.
 *
 * @since 1.0.0
 */
public record SubmitRequestCommand(Long requesterId, Long requestTypeId, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, List<FieldValue> fieldValues, List<FileReference> attachments) {
    /**
     * Compact constructor for SubmitRequestCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public SubmitRequestCommand {
        if (requesterId == null || requesterId <= 0) {
            throw new IllegalArgumentException("requesterId cannot be null or less than 1");
        }
        if (requestTypeId == null || requestTypeId <= 0) {
            throw new IllegalArgumentException("requestTypeId cannot be null or less than 1");
        }
        fieldValues = fieldValues == null ? List.of() : List.copyOf(fieldValues);
        attachments = attachments == null ? List.of() : List.copyOf(attachments);
    }
}
