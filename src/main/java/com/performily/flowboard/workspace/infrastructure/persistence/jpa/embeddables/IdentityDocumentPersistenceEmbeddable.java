package com.performily.flowboard.workspace.infrastructure.persistence.jpa.embeddables;

import com.performily.flowboard.workspace.domain.model.valueobjects.IdentityDocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Identity Document Persistence Embeddable
 * @summary
 * Persistence representation for the IdentityDocument value object.
 *
 * @since 1.0.0
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdentityDocumentPersistenceEmbeddable {
    @Enumerated(EnumType.STRING)
    @Column(name = "identity_document_type", nullable = false, length = 10)
    private IdentityDocumentType type;

    @Column(name = "identity_document_number", nullable = false, length = 12)
    private String number;
}