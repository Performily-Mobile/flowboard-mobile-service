package com.performily.flowboard.benefits.domain.model.aggregates;

import com.performily.flowboard.benefits.domain.model.entities.VacationMovement;
import com.performily.flowboard.benefits.domain.model.events.VacationBalanceUpdatedEvent;
import com.performily.flowboard.benefits.domain.model.valueobjects.VacationDays;
import com.performily.flowboard.benefits.domain.model.valueobjects.VacationMovementType;
import com.performily.flowboard.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.shared.domain.model.valueobjects.RequestId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Vacation Balance Aggregate Root
 * @summary
 * Vacation days of one employee with the movements that explain them.
 *
 * Invariants checked by the aggregate:
 * - availableDays = accruedDays - usedDays, and it is never negative.
 * - A usage (debit) is rejected when availableDays is lower than the requested days.
 * - A request can be debited only once, and only a registered usage can be reversed (once).
 * - A manual adjustment needs a reason and an author, and cannot leave the balance negative.
 * - The monthly accrual is applied at most once per month (lastAccrualDate).
 * - Every change adds a VacationMovement and a VacationBalanceUpdatedEvent.
 *
 * One balance per employee is checked by the application service.
 *
 * @since 1.0.0
 */
public class VacationBalance extends AbstractDomainAggregateRoot<VacationBalance> {
    private static final String INITIAL_ACCRUAL_REASON = "Acumulación inicial por antigüedad";
    private static final String MONTHLY_ACCRUAL_REASON = "Acumulación mensual";

    private Long id;
    private final EmployeeId employeeId;
    private VacationDays accruedDays;
    private VacationDays usedDays;
    private LocalDate lastAccrualDate;
    private final List<VacationMovement> movements;

    /**
     * Opens an empty balance for an employee.
     *
     * @param employeeId the owner of the balance
     */
    public VacationBalance(EmployeeId employeeId) {
        this.employeeId = Objects.requireNonNull(employeeId, "Employee is required");
        this.accruedDays = VacationDays.ZERO;
        this.usedDays = VacationDays.ZERO;
        this.movements = new ArrayList<>();
    }

    /**
     * Rebuilds a stored balance.
     */
    public VacationBalance(Long id, EmployeeId employeeId, VacationDays accruedDays, VacationDays usedDays,
                           LocalDate lastAccrualDate, List<VacationMovement> movements) {
        this.id = id;
        this.employeeId = employeeId;
        this.accruedDays = accruedDays;
        this.usedDays = usedDays;
        this.lastAccrualDate = lastAccrualDate;
        this.movements = new ArrayList<>(movements);
    }

    /**
     * Opens the balance of an employee with the days already earned by seniority.
     *
     * @param employeeId     the owner of the balance
     * @param initialAccrual the days earned before the balance existed, can be zero
     * @param openedOn       the date of the opening, it counts as the accrual of that month
     * @return the new balance
     */
    public static VacationBalance open(EmployeeId employeeId, VacationDays initialAccrual, LocalDate openedOn) {
        var balance = new VacationBalance(employeeId);
        if (initialAccrual != null && !initialAccrual.isZero()) {
            balance.accrue(initialAccrual, INITIAL_ACCRUAL_REASON, openedOn);
        } else {
            balance.lastAccrualDate = openedOn;
        }
        return balance;
    }

    public VacationDays availableDays() {
        return accruedDays.minus(usedDays);
    }

    public boolean hasEnough(VacationDays days) {
        return days != null && !availableDays().isLessThan(days);
    }

    /**
     * Adds days earned by seniority.
     *
     * @param days        the earned days, greater than zero
     * @param reason      the description of the accrual
     * @param accrualDate the date of the accrual
     */
    public void accrue(VacationDays days, String reason, LocalDate accrualDate) {
        requirePositive(days);
        Objects.requireNonNull(accrualDate, "Accrual date is required");
        accruedDays = accruedDays.plus(days);
        lastAccrualDate = accrualDate;
        addMovement(VacationMovementType.ACCRUAL, days.value(), reason, null, null);
    }

    /**
     * Adds the days of one month, only if that month was not accrued yet.
     *
     * @param days        the days of one month
     * @param accrualDate any date of the month to accrue
     * @return true when the days were added, false when the month was already accrued
     */
    public boolean accrueMonthly(VacationDays days, LocalDate accrualDate) {
        Objects.requireNonNull(accrualDate, "Accrual date is required");
        if (isMonthAccrued(accrualDate)) {
            return false;
        }
        accrue(days, MONTHLY_ACCRUAL_REASON, accrualDate);
        return true;
    }

    public boolean isMonthAccrued(LocalDate date) {
        return lastAccrualDate != null && !lastAccrualDate.isBefore(date.withDayOfMonth(1));
    }

    /**
     * Takes days because a vacation request was approved (USAGE).
     *
     * @param days      the days of the request, greater than zero
     * @param requestId the approved request
     */
    public void debit(VacationDays days, RequestId requestId) {
        requirePositive(days);
        Objects.requireNonNull(requestId, "Request is required");
        if (hasUsageFor(requestId)) {
            throw new IllegalStateException("Request %d was already debited".formatted(requestId.value()));
        }
        if (!hasEnough(days)) {
            throw new IllegalStateException("Not enough vacation days: %s available, %s requested"
                    .formatted(availableDays(), days));
        }
        usedDays = usedDays.plus(days);
        addMovement(VacationMovementType.USAGE, days.value().negate(), null, null, requestId);
    }

    /**
     * Returns the days of an approved request that was annulled (REVERSAL).
     *
     * @param days      the days to return, they must match the registered usage
     * @param requestId the annulled request
     */
    public void revertDebit(VacationDays days, RequestId requestId) {
        requirePositive(days);
        Objects.requireNonNull(requestId, "Request is required");
        var usage = movements.stream().filter(m -> m.isUsageOf(requestId)).findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Request %d has no registered usage to reverse".formatted(requestId.value())));
        if (hasReversalFor(requestId)) {
            throw new IllegalStateException("Request %d was already reversed".formatted(requestId.value()));
        }
        if (usage.getDays().negate().compareTo(days.value()) != 0) {
            throw new IllegalArgumentException("Request %d used %s days, it cannot reverse %s"
                    .formatted(requestId.value(), usage.getDays().negate().stripTrailingZeros().toPlainString(), days));
        }
        usedDays = usedDays.minus(days);
        addMovement(VacationMovementType.REVERSAL, days.value(), null, null, requestId);
    }

    /**
     * Reverses the usage of a request with the same days that were taken.
     *
     * @param requestId the annulled request
     */
    public void reverseUsageOf(RequestId requestId) {
        var usage = movements.stream().filter(m -> m.isUsageOf(requestId)).findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Request %d has no registered usage to reverse".formatted(requestId.value())));
        revertDebit(new VacationDays(usage.getDays().negate()), requestId);
    }

    /**
     * Manual correction made by HR staff (MANUAL_ADJUSTMENT). Positive days add to the
     * accrued days and negative days remove them, without leaving the balance negative.
     *
     * @param days     the signed days, not zero and with at most 2 decimals
     * @param reason   the reason of the adjustment, required (max 250)
     * @param authorId the HR employee that makes it, required
     */
    public void adjust(BigDecimal days, String reason, EmployeeId authorId) {
        if (days == null || days.signum() == 0) {
            throw new IllegalArgumentException("Adjustment days cannot be zero");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Adjustment reason is required");
        }
        if (reason.trim().length() > 250) {
            throw new IllegalArgumentException("Adjustment reason cannot exceed 250 characters");
        }
        Objects.requireNonNull(authorId, "Adjustment author is required");
        var amount = new VacationDays(days.abs());
        if (days.signum() > 0) {
            accruedDays = accruedDays.plus(amount);
        } else {
            if (!hasEnough(amount)) {
                throw new IllegalStateException("The adjustment would leave the balance negative: %s available, %s to discount"
                        .formatted(availableDays(), amount));
            }
            accruedDays = accruedDays.minus(amount);
        }
        addMovement(VacationMovementType.MANUAL_ADJUSTMENT, amount.value().multiply(BigDecimal.valueOf(days.signum())),
                reason.trim(), authorId, null);
    }

    public boolean hasUsageFor(RequestId requestId) {
        return movements.stream().anyMatch(m -> m.isUsageOf(requestId));
    }

    public boolean hasReversalFor(RequestId requestId) {
        return movements.stream().anyMatch(m -> m.isReversalOf(requestId));
    }

    /**
     * Movements from the newest to the oldest, as shown in the app.
     *
     * @return the movements of the balance
     */
    public List<VacationMovement> getMovementsNewestFirst() {
        return movements.stream()
                .sorted(Comparator.comparing(VacationMovement::getOccurredAt).reversed())
                .toList();
    }

    private void addMovement(VacationMovementType type, BigDecimal days, String reason, EmployeeId authorId,
                             RequestId requestId) {
        var now = LocalDateTime.now();
        movements.add(new VacationMovement(null, type, days, reason, authorId, requestId, now));
        registerDomainEvent(new VacationBalanceUpdatedEvent(employeeId.value(), type.name(), days,
                availableDays().value(), now));
    }

    private static void requirePositive(VacationDays days) {
        if (days == null || days.isZero()) {
            throw new IllegalArgumentException("Days must be greater than zero");
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EmployeeId getEmployeeId() { return employeeId; }
    public VacationDays getAccruedDays() { return accruedDays; }
    public VacationDays getUsedDays() { return usedDays; }
    public LocalDate getLastAccrualDate() { return lastAccrualDate; }
    public List<VacationMovement> getMovements() { return List.copyOf(movements); }
}
