package com.performily.flowboard.request.domain.model.commands;

import com.performily.flowboard.request.domain.model.valueobjects.FieldValue;
import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;

import java.util.List;

/**
 * Resubmit Request Command
 * @summary
 * Command to send again a request that was returned for review.
 *
 * @since 1.0.0
 */
public record ResubmitRequestCommand(Long requestId, Long actorId, List<FieldValue> fieldValues, List<FileReference> attachments, String comment) {
    /**
     * Compact constructor for ResubmitRequestCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public ResubmitRequestCommand {
        if (requestId == null || requestId <= 0) {
            throw new IllegalArgumentException("requestId cannot be null or less than 1");
        }
        if (actorId == null || actorId <= 0) {
            throw new IllegalArgumentException("actorId cannot be null or less than 1");
        }
        fieldValues = fieldValues == null ? List.of() : List.copyOf(fieldValues);
        attachments = attachments == null ? List.of() : List.copyOf(attachments);
    }
}
