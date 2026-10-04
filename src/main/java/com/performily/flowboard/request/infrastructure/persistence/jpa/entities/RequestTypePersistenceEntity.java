package com.performily.flowboard.request.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.request.domain.model.valueobjects.BalanceDeduction;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Request Type Persistence Entity
 * @summary
 * JPA persistence entity for request types.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "request_types")
@Getter
@Setter
@NoArgsConstructor
public class RequestTypePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "requires_attachment", nullable = false)
    private boolean requiresAttachment;

    @Enumerated(EnumType.STRING)
    @Column(name = "balance_deduction", nullable = false, length = 20)
    private BalanceDeduction balanceDeduction;

    @Column(nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "requestType", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC, id ASC")
    private List<RequestFieldPersistenceEntity> fields = new ArrayList<>();
}
