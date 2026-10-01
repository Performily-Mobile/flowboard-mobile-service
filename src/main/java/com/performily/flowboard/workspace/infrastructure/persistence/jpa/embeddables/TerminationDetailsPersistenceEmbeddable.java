package com.performily.flowboard.workspace.infrastructure.persistence.jpa.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Termination Details Persistence Embeddable
 * @summary
 * Persistence representation for the TerminationDetails value object. Both columns are null while the employee is not terminated.
 *
 * @since 1.0.0
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TerminationDetailsPersistenceEmbeddable {
    @Column(name = "termination_reason", length = 500)
    private String reason;

    @Column(name = "termination_date")
    private LocalDate terminationDate;
}