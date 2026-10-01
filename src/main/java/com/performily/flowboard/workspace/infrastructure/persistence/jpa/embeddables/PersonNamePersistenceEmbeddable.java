package com.performily.flowboard.workspace.infrastructure.persistence.jpa.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Person Name Persistence Embeddable
 * @summary
 * Persistence representation for the PersonName value object.
 *
 * @since 1.0.0
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonNamePersistenceEmbeddable {
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;
}