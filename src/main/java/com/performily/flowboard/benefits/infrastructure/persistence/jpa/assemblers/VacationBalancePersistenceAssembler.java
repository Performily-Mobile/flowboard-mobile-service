package com.performily.flowboard.benefits.infrastructure.persistence.jpa.assemblers;

import com.performily.flowboard.benefits.domain.model.aggregates.VacationBalance;
import com.performily.flowboard.benefits.domain.model.entities.VacationMovement;
import com.performily.flowboard.benefits.domain.model.valueobjects.VacationDays;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities.VacationBalancePersistenceEntity;
import com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities.VacationMovementPersistenceEntity;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.shared.domain.model.valueobjects.RequestId;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Converts between the VacationBalance aggregate (with its movements) and its persistence entities.
 */
public final class VacationBalancePersistenceAssembler {
    private VacationBalancePersistenceAssembler() {
    }

    public static VacationBalance toDomainFromPersistence(VacationBalancePersistenceEntity entity) {
        var movements = entity.getMovements().stream()
                .map(VacationBalancePersistenceAssembler::toDomainFromPersistence)
                .toList();
        return new VacationBalance(
                entity.getId(),
                new EmployeeId(entity.getEmployeeId()),
                new VacationDays(entity.getAccruedDays()),
                new VacationDays(entity.getUsedDays()),
                entity.getLastAccrualDate(),
                movements);
    }

    public static VacationBalancePersistenceEntity toPersistenceFromDomain(VacationBalance balance) {
        var entity = new VacationBalancePersistenceEntity();
        entity.setId(balance.getId());
        entity.setEmployeeId(balance.getEmployeeId().value());
        entity.setAccruedDays(balance.getAccruedDays().value());
        entity.setUsedDays(balance.getUsedDays().value());
        entity.setLastAccrualDate(balance.getLastAccrualDate());
        entity.setMovements(balance.getMovements().stream()
                .map(movement -> toPersistenceFromDomain(movement, entity))
                .collect(Collectors.toCollection(ArrayList::new)));
        return entity;
    }

    private static VacationMovement toDomainFromPersistence(VacationMovementPersistenceEntity entity) {
        return new VacationMovement(
                entity.getId(),
                entity.getType(),
                entity.getDays(),
                entity.getReason(),
                entity.getAuthorId() == null ? null : new EmployeeId(entity.getAuthorId()),
                entity.getRequestId() == null ? null : new RequestId(entity.getRequestId()),
                entity.getOccurredAt());
    }

    private static VacationMovementPersistenceEntity toPersistenceFromDomain(VacationMovement movement,
                                                                            VacationBalancePersistenceEntity balance) {
        var entity = new VacationMovementPersistenceEntity();
        entity.setId(movement.getId());
        entity.setVacationBalance(balance);
        entity.setType(movement.getType());
        entity.setDays(movement.getDays());
        entity.setReason(movement.getReason());
        entity.setAuthorId(movement.getAuthorId() == null ? null : movement.getAuthorId().value());
        entity.setRequestId(movement.getRequestId() == null ? null : movement.getRequestId().value());
        entity.setOccurredAt(movement.getOccurredAt());
        return entity;
    }
}
