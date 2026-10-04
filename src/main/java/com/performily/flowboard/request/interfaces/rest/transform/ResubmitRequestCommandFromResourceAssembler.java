package com.performily.flowboard.request.interfaces.rest.transform;

import com.performily.flowboard.request.domain.model.commands.ResubmitRequestCommand;
import com.performily.flowboard.request.interfaces.rest.resources.ResubmitRequestResource;

/**
 * Resubmit Request Command From Resource Assembler
 * @summary
 * Assembler to convert a ResubmitRequestResource to a ResubmitRequestCommand.
 *
 * @since 1.0.0
 */
public class ResubmitRequestCommandFromResourceAssembler {
    /**
     * Converts a {@link ResubmitRequestResource} to a {@link ResubmitRequestCommand}.
     *
     * @param requestId the request id
     * @param resource the {@link ResubmitRequestResource} instance
     * @return the {@link ResubmitRequestCommand}
     */
    public static ResubmitRequestCommand toCommandFromResource(Long requestId, ResubmitRequestResource resource) {
        return new ResubmitRequestCommand(
                requestId,
                resource.actorId(),
                FieldValueAndAttachmentAssembler.toFieldValues(resource.fieldValues()),
                FieldValueAndAttachmentAssembler.toFileReferences(resource.attachments()),
                resource.comment());
    }
}
