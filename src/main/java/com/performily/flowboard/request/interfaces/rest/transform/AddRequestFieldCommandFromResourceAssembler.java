package com.performily.flowboard.request.interfaces.rest.transform;

import com.performily.flowboard.request.domain.model.commands.AddRequestFieldCommand;
import com.performily.flowboard.request.domain.model.valueobjects.FieldDataType;
import com.performily.flowboard.request.interfaces.rest.resources.CreateRequestFieldResource;

/**
 * Add Request Field Command From Resource Assembler
 * @summary
 * Assembler to convert a CreateRequestFieldResource to an AddRequestFieldCommand.
 *
 * @since 1.0.0
 */
public class AddRequestFieldCommandFromResourceAssembler {
    /**
     * Converts a {@link CreateRequestFieldResource} to a {@link AddRequestFieldCommand}.
     *
     * @param requestTypeId the request type id
     * @param resource the {@link CreateRequestFieldResource} instance
     * @return the {@link AddRequestFieldCommand}
     */
    public static AddRequestFieldCommand toCommandFromResource(Long requestTypeId, CreateRequestFieldResource resource) {
        return new AddRequestFieldCommand(
                requestTypeId,
                resource.key(),
                resource.label(),
                RequestEnumAssembler.toEnum(FieldDataType.class, resource.dataType(), null, "dataType"),
                Boolean.TRUE.equals(resource.required()),
                resource.displayOrder() == null ? 0 : resource.displayOrder());
    }
}
