package com.performily.flowboard.request.interfaces.rest.transform;

import com.performily.flowboard.request.domain.model.commands.UpdateRequestTypeCommand;
import com.performily.flowboard.request.domain.model.valueobjects.BalanceDeduction;
import com.performily.flowboard.request.interfaces.rest.resources.UpdateRequestTypeResource;

/**
 * Update Request Type Command From Resource Assembler
 * @summary
 * Assembler to convert an UpdateRequestTypeResource to an UpdateRequestTypeCommand.
 *
 * @since 1.0.0
 */
public class UpdateRequestTypeCommandFromResourceAssembler {
    /**
     * Converts a {@link UpdateRequestTypeResource} to a {@link UpdateRequestTypeCommand}.
     *
     * @param requestTypeId the request type id
     * @param resource the {@link UpdateRequestTypeResource} instance
     * @return the {@link UpdateRequestTypeCommand}
     */
    public static UpdateRequestTypeCommand toCommandFromResource(Long requestTypeId, UpdateRequestTypeResource resource) {
        return new UpdateRequestTypeCommand(
                requestTypeId,
                resource.name(),
                resource.description(),
                Boolean.TRUE.equals(resource.requiresAttachment()),
                RequestEnumAssembler.toEnum(BalanceDeduction.class, resource.balanceDeduction(),
                        BalanceDeduction.NONE, "balanceDeduction"));
    }
}
