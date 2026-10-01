package com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.shared.infrastructure.persistence.jpa.embeddables.FileReferencePersistenceEmbeddable;
import com.performily.flowboard.workspace.domain.model.valueobjects.DocumentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Employee Document Persistence Entity
 * @summary
 * JPA persistence entity for employee documents.
 *
 * It does not extend AuditableAbstractPersistenceEntity because the
 * employee_documents table only keeps uploaded_at.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "employee_documents")
@Getter
@Setter
@NoArgsConstructor
public class EmployeeDocumentPersistenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeePersistenceEntity employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 40)
    private DocumentType documentType;

    @Embedded
    private FileReferencePersistenceEmbeddable file;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;
}