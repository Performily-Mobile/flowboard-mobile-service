package com.performily.flowboard.wellbeing.interfaces.rest.transform;

import com.performily.flowboard.wellbeing.domain.model.aggregates.Office;
import com.performily.flowboard.wellbeing.interfaces.rest.resources.OfficeResource;

public class OfficeResourceFromEntityAssembler {
    public static OfficeResource toResourceFromEntity(Office o) {
        return new OfficeResource(o.getId(), o.getName(), o.getArea(), o.getLocation().address(),
                o.getLocation().floor(), o.getLocation().reference(), o.isActive());
    }
}
