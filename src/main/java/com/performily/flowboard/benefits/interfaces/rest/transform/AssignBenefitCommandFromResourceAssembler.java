package com.performily.flowboard.benefits.interfaces.rest.transform;

import com.performily.flowboard.benefits.domain.model.commands.AssignBenefitCommand;
import com.performily.flowboard.benefits.domain.model.commands.AssignBenefitToAreaCommand;
import com.performily.flowboard.benefits.interfaces.rest.resources.AssignBenefitResource;

/**
 * Turns an assignment request into the command for one employee or for an area.
 */
public final class AssignBenefitCommandFromResourceAssembler {
    private AssignBenefitCommandFromResourceAssembler() {
    }

    public static boolean isForArea(AssignBenefitResource resource) {
        boolean hasEmployee = resource.employeeId() != null;
        boolean hasArea = resource.areaId() != null;
        if (hasEmployee == hasArea) {
            throw new IllegalArgumentException("Send employeeId to assign to one employee or areaId to assign to an area, not both");
        }
        return hasArea;
    }

    public static AssignBenefitCommand toCommandFromResource(AssignBenefitResource resource) {
        return new AssignBenefitCommand(resource.benefitTypeId(), resource.employeeId(), resource.quantity(),
                resource.startDate(), resource.endDate());
    }

    public static AssignBenefitToAreaCommand toAreaCommandFromResource(AssignBenefitResource resource) {
        return new AssignBenefitToAreaCommand(resource.benefitTypeId(), resource.areaId(), resource.quantity(),
                resource.startDate(), resource.endDate());
    }
}
