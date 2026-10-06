package com.performily.flowboard.benefits.interfaces.rest.transform;

import com.performily.flowboard.benefits.application.commandservices.AreaAssignmentResult;
import com.performily.flowboard.benefits.application.queryservices.views.AreaAssignmentPreview;
import com.performily.flowboard.benefits.application.queryservices.views.BenefitAssignmentView;
import com.performily.flowboard.benefits.application.queryservices.views.EmployeeBenefitsView;
import com.performily.flowboard.benefits.domain.model.aggregates.BenefitAssignment;
import com.performily.flowboard.benefits.domain.model.valueobjects.BenefitUnit;
import com.performily.flowboard.benefits.interfaces.rest.resources.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Builds the REST resources of the assignments. displayQuantity is the quantity
 * ready to show in the app: "S/ 150.00", "1 día", "2 unidades".
 */
public final class BenefitAssignmentResourceFromEntityAssembler {
    private BenefitAssignmentResourceFromEntityAssembler() {
    }

    public static BenefitAssignmentResource toResourceFromEntity(BenefitAssignment entity) {
        return toResourceFromEntity(entity, null);
    }

    public static BenefitAssignmentResource toResourceFromView(BenefitAssignmentView view) {
        return toResourceFromEntity(view.assignment(), view.employeeName());
    }

    private static BenefitAssignmentResource toResourceFromEntity(BenefitAssignment entity, String employeeName) {
        var delivery = entity.getDelivery() == null ? null : new BenefitDeliveryResource(
                entity.getDelivery().getDeliveredOn(),
                entity.getDelivery().getRegisteredBy() == null ? null : entity.getDelivery().getRegisteredBy().value(),
                entity.getDelivery().getNotes());
        var unit = entity.getBenefitType().getUnit();
        return new BenefitAssignmentResource(
                entity.getId(),
                entity.getBenefitType().getId(),
                entity.getBenefitType().getName(),
                unit.name(),
                entity.getEmployeeId().value(),
                employeeName,
                entity.getSourceAreaId() == null ? null : entity.getSourceAreaId().value(),
                entity.getQuantity().value(),
                displayQuantity(entity.getQuantity().value(), unit),
                entity.getValidity().startDate(),
                entity.getValidity().endDate(),
                entity.getStatus().name(),
                delivery);
    }

    public static BenefitAssignmentBatchResource toBatchResource(BenefitAssignment single) {
        return new BenefitAssignmentBatchResource(null, 1, 0, List.of(), List.of(toResourceFromEntity(single)));
    }

    public static BenefitAssignmentBatchResource toBatchResource(AreaAssignmentResult result) {
        return new BenefitAssignmentBatchResource(result.areaId(), result.assignments().size(),
                result.skippedEmployeeIds().size(), result.skippedEmployeeIds(),
                result.assignments().stream().map(BenefitAssignmentResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    public static AreaAssignmentPreviewResource toResourceFromView(AreaAssignmentPreview preview) {
        return new AreaAssignmentPreviewResource(preview.areaId(), preview.activeEmployees(),
                preview.alreadyAssigned(), preview.toAssign());
    }

    public static EmployeeBenefitsResource toResourceFromView(EmployeeBenefitsView view) {
        var current = view.current().stream().map(BenefitAssignmentResourceFromEntityAssembler::toResourceFromEntity).toList();
        var delivered = view.delivered().stream().map(BenefitAssignmentResourceFromEntityAssembler::toResourceFromEntity).toList();
        return new EmployeeBenefitsResource(view.employeeId(), current.size(), delivered.size(), current, delivered);
    }

    static String displayQuantity(BigDecimal quantity, BenefitUnit unit) {
        var plain = quantity.stripTrailingZeros().toPlainString();
        boolean one = quantity.compareTo(BigDecimal.ONE) == 0;
        return switch (unit) {
            case MONEY -> "S/ " + quantity.toPlainString();
            case DAYS -> plain + (one ? " día" : " días");
            case UNITS -> plain + (one ? " unidad" : " unidades");
        };
    }
}
