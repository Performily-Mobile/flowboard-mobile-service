package com.performily.flowboard.benefits.interfaces.rest.transform;

import com.performily.flowboard.benefits.application.queryservices.views.VacationBalanceView;
import com.performily.flowboard.benefits.application.queryservices.views.VacationMovementView;
import com.performily.flowboard.benefits.domain.model.aggregates.VacationBalance;
import com.performily.flowboard.benefits.domain.model.commands.AdjustVacationBalanceCommand;
import com.performily.flowboard.benefits.interfaces.rest.resources.AdjustVacationBalanceResource;
import com.performily.flowboard.benefits.interfaces.rest.resources.VacationBalanceResource;
import com.performily.flowboard.benefits.interfaces.rest.resources.VacationMovementResource;

import java.util.List;
import java.util.Locale;

public final class VacationBalanceResourceFromEntityAssembler {
    private VacationBalanceResourceFromEntityAssembler() {
    }

    public static VacationBalanceResource toResourceFromView(VacationBalanceView view) {
        var balance = view.balance();
        return new VacationBalanceResource(
                balance.getEmployeeId().value(),
                view.employeeName(),
                view.areaName(),
                balance.getAccruedDays().value(),
                balance.getUsedDays().value(),
                balance.availableDays().value(),
                balance.getLastAccrualDate(),
                view.movements().stream().map(VacationBalanceResourceFromEntityAssembler::toResourceFromView).toList());
    }

    /**
     * Used after an adjustment: the balance with its movements, without names.
     */
    public static VacationBalanceResource toResourceFromEntity(VacationBalance balance) {
        var movements = balance.getMovementsNewestFirst().stream()
                .map(movement -> new VacationMovementView(movement, null)).toList();
        return toResourceFromView(new VacationBalanceView(balance, null, null, movements));
    }

    public static VacationMovementResource toResourceFromView(VacationMovementView view) {
        var movement = view.movement();
        return new VacationMovementResource(
                movement.getId(),
                movement.getType().name(),
                movement.getDays(),
                movement.getReason(),
                movement.getAuthorId() == null ? null : movement.getAuthorId().value(),
                view.authorName(),
                movement.getRequestId() == null ? null : movement.getRequestId().value(),
                movement.getOccurredAt());
    }

    public static List<VacationMovementResource> toResourcesFromViews(List<VacationMovementView> views) {
        return views.stream().map(VacationBalanceResourceFromEntityAssembler::toResourceFromView).toList();
    }

    public static AdjustVacationBalanceCommand toCommandFromResource(Long employeeId, AdjustVacationBalanceResource resource) {
        var operation = resource.operation() == null ? "" : resource.operation().trim().toUpperCase(Locale.ROOT);
        var days = switch (operation) {
            case "ADD" -> resource.days();
            case "DEDUCT" -> resource.days().negate();
            default -> throw new IllegalArgumentException(
                    "Invalid value '%s' for operation. Allowed values: ADD, DEDUCT".formatted(resource.operation()));
        };
        return new AdjustVacationBalanceCommand(employeeId, days, resource.reason(), resource.authorId());
    }
}
