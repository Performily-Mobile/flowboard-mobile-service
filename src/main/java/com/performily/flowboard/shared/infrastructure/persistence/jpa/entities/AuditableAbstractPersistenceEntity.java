package com.performily.flowboard.shared.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * Base JPA persistence entity for all persistence entities that require auditing.
 *
 * <p>Provides {@code id}, {@code createdAt}, and {@code updatedAt} fields.
 * This class intentionally lives in the infrastructure layer to keep JPA and
 * Spring Data auditing concerns out of the domain model.</p>
 *
 * <p>All bounded-context JPA persistence entities should extend this class
 * instead of placing {@code @Id} and auditing fields directly.</p>
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableAbstractPersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Date updatedAt;

    /**
     * Sets the id. Used by assemblers when reconstructing a persistence entity
     * from an existing domain object that already carries an identity.
     *
     * @param id the persistence identity to assign
     */
    public void setId(Long id) {
        this.id = id;
    }
}

