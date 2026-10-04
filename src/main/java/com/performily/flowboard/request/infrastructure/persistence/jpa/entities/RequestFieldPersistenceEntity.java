package com.performily.flowboard.request.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.request.domain.model.valueobjects.FieldDataType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request Field Persistence Entity
 * @summary
 * JPA persistence entity for the fields of a request type.
 *
 * It does not extend AuditableAbstractPersistenceEntity because the
 * request_fields table has no audit columns.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "request_fields",
        uniqueConstraints = @UniqueConstraint(columnNames = {"request_type_id", "field_key"}))
@Getter
@Setter
@NoArgsConstructor
public class RequestFieldPersistenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_type_id", nullable = false)
    private RequestTypePersistenceEntity requestType;

    @Column(name = "field_key", nullable = false, length = 50)
    private String fieldKey;

    @Column(nullable = false, length = 80)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false, length = 10)
    private FieldDataType dataType;

    @Column(nullable = false)
    private boolean required;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;
}
