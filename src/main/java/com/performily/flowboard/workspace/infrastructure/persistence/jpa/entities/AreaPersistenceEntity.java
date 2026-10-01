package com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Area Persistence Entity
 * @summary
 * JPA persistence entity for areas.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "areas")
@Getter
@Setter
@NoArgsConstructor
public class AreaPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private boolean active;
}