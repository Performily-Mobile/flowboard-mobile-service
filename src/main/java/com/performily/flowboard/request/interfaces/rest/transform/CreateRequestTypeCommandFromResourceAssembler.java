package com.performily.flowboard.request.interfaces.rest.transform;

import com.performily.flowboard.request.domain.model.commands.CreateRequestTypeCommand;
import com.performily.flowboard.request.domain.model.entities.RequestField;
import com.performily.flowboard.request.domain.model.valueobjects.BalanceDeduction;
import com.performily.flowboard.request.domain.model.valueobjects.FieldDataType;
import com.performily.flowboard.request.domain.model.valueobjects.FieldKey;
import com.performily.flowboard.request.interfaces.rest.resources.CreateRequestFieldResource;
import com.performily.flowboard.request.interfaces.rest.resources.CreateRequestTypeResource;

import java.util.List;

/**
 * Create Request Type Command From Resource Assembler
 * @summary
 * Assembler to convert a CreateRequestTypeResource to a CreateRequestTypeCommand.
 *
 * @since 1.0.0
 */
public class CreateRequestTypeCommandFromResourceAssembler {
    /**
     * Converts a {@link CreateRequestTypeResource} to a {@link CreateRequestTypeCommand}.
     *
     * @param resource the {@link CreateRequestTypeResource} instance
     * @return the {@link CreateRequestTypeCommand}
     */
    public static CreateRequestTypeCommand toCommandFromResource(CreateRequestTypeResource resource) {
        var fields = resource.fields() == null
                ? List.<RequestField>of()
                : resource.fields().stream().map(CreateRequestTypeCommandFromResourceAssembler::toField).toList();
        return new CreateRequestTypeCommand(
                resource.name(),
                resource.description(),
                Boolean.TRUE.equals(resource.requiresAttachment()),
                RequestEnumAssembler.toEnum(BalanceDeduction.class, resource.balanceDeduction(),
                        BalanceDeduction.NONE, "balanceDeduction"),
                fields);
    }

    private static RequestField toField(CreateRequestFieldResource resource) {
        return new RequestField(
                new FieldKey(resource.key()),
                resource.label(),
                RequestEnumAssembler.toEnum(FieldDataType.class, resource.dataType(), null, "dataType"),
                Boolean.TRUE.equals(resource.required()),
                resource.displayOrder() == null ? 0 : resource.displayOrder());
    }
}
