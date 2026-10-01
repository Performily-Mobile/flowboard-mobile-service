package com.performily.flowboard.workspace.infrastructure.persistence.jpa.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Employment Period Persistence Embeddable
 * @summary
 * Persistence representation for the EmploymentPeriod value object.
 *
 * @since 1.0.0
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentPeriodPersistenceEmbeddable {
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;
}