package com.performily.flowboard.benefits.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistence entity of a vacation balance (table vacation_balances).
 * The unique employee_id keeps one balance per employee; it is a logical reference to Workspace.
 */
@Entity
@Table(name = "vacation_balances")
public class VacationBalancePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "employee_id", nullable = false, unique = true)
    private Long employeeId;

    @Column(name = "accrued_days", nullable = false, precision = 6, scale = 2)
    private BigDecimal accruedDays;

    @Column(name = "used_days", nullable = false, precision = 6, scale = 2)
    private BigDecimal usedDays;

    @Column(name = "last_accrual_date")
    private LocalDate lastAccrualDate;

    @OneToMany(mappedBy = "vacationBalance", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("occurredAt ASC, id ASC")
    private List<VacationMovementPersistenceEntity> movements = new ArrayList<>();

    public VacationBalancePersistenceEntity() {
    }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public BigDecimal getAccruedDays() { return accruedDays; }
    public void setAccruedDays(BigDecimal accruedDays) { this.accruedDays = accruedDays; }

    public BigDecimal getUsedDays() { return usedDays; }
    public void setUsedDays(BigDecimal usedDays) { this.usedDays = usedDays; }

    public LocalDate getLastAccrualDate() { return lastAccrualDate; }
    public void setLastAccrualDate(LocalDate lastAccrualDate) { this.lastAccrualDate = lastAccrualDate; }

    public List<VacationMovementPersistenceEntity> getMovements() { return movements; }
    public void setMovements(List<VacationMovementPersistenceEntity> movements) { this.movements = movements; }
}
