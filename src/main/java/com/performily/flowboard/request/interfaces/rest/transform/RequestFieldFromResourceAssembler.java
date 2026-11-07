package com.performily.flowboard.request.interfaces.rest.transform;

import com.performily.flowboard.request.domain.model.entities.RequestField;
import com.performily.flowboard.request.domain.model.valueobjects.FieldDataType;
import com.performily.flowboard.request.domain.model.valueobjects.FieldKey;
import com.performily.flowboard.request.interfaces.rest.resources.CreateRequestFieldResource;

import java.util.List;

/**
 * Request Field From Resource Assembler
 * @summary
 * Assembler to convert CreateRequestFieldResource items into RequestField entities.
 *
 * @since 1.0.0
 */
public class RequestFieldFromResourceAssembler {
    /**
     * Converts a list of field resources into fields. A null list returns null.
     *
     * @param resources the list of {@link CreateRequestFieldResource}
     * @return the list of {@link RequestField}, or null
     */
    public static List<RequestField> toFieldsFromResources(List<CreateRequestFieldResource> resources) {
        return resources == null ? null : resources.stream().map(RequestFieldFromResourceAssembler::toFieldFromResource).toList();
    }

    /**
     * Converts a {@link CreateRequestFieldResource} to a {@link RequestField}.
     *
     * @param resource the {@link CreateRequestFieldResource} instance
     * @return the {@link RequestField}
     */
    public static RequestField toFieldFromResource(CreateRequestFieldResource resource) {
        return new RequestField(
                new FieldKey(resource.key()),
                resource.label(),
                RequestEnumAssembler.toEnum(FieldDataType.class, resource.dataType(), null, "dataType"),
                Boolean.TRUE.equals(resource.required()),
                resource.displayOrder() == null ? 0 : resource.displayOrder());
    }
}
