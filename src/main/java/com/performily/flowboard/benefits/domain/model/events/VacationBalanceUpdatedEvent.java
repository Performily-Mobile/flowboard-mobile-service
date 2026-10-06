package com.performily.flowboard.benefits.domain.model.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain event registered every time a vacation balance changes
 * ("Saldo de Vacaciones Actualizado" in the Bounded Context Canvas).
 *
 * @param employeeId    the owner of the balance
 * @param movementType  the type of the movement (ACCRUAL, USAGE, REVERSAL, MANUAL_ADJUSTMENT)
 * @param days          the signed effect of the movement
 * @param availableDays the available days after the movement
 * @param occurredOn    when it happened
 */
public record VacationBalanceUpdatedEvent(Long employeeId, String movementType, BigDecimal days,
                                          BigDecimal availableDays, LocalDateTime occurredOn) {
}
