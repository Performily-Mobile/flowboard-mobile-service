package com.performily.flowboard.request.interfaces.rest.transform;

import com.performily.flowboard.request.domain.model.commands.SubmitRequestCommand;
import com.performily.flowboard.request.interfaces.rest.resources.SubmitRequestResource;

/**
 * Submit Request Command From Resource Assembler
 * @summary
 * Assembler to convert a SubmitRequestResource to a SubmitRequestCommand.
 *
 * @since 1.0.0
 */
public class SubmitRequestCommandFromResourceAssembler {
    /**
     * Converts a {@link SubmitRequestResource} to a {@link SubmitRequestCommand}.
     *
     * @param resource the {@link SubmitRequestResource} instance
     * @return the {@link SubmitRequestCommand}
     */
    public static SubmitRequestCommand toCommandFromResource(SubmitRequestResource resource) {
        return new SubmitRequestCommand(
                resource.requesterId(),
                resource.requestTypeId(),
                resource.startDate(),
                resource.endDate(),
                resource.startTime(),
                resource.endTime(),
                FieldValueAndAttachmentAssembler.toFieldValues(resource.fieldValues()),
                FieldValueAndAttachmentAssembler.toFileReferences(resource.attachments()));
    }
}
