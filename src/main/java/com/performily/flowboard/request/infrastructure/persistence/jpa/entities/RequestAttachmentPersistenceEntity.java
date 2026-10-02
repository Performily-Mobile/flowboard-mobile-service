package com.performily.flowboard.request.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.shared.infrastructure.persistence.jpa.embeddables.FileReferencePersistenceEmbeddable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Request Attachment Persistence Entity
 * @summary
 * JPA persistence entity for the attachments of a request. Only the metadata of
 * the file is stored; the file itself lives in the storage service.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "request_attachments")
@Getter
@Setter
@NoArgsConstructor
public class RequestAttachmentPersistenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private RequestPersistenceEntity request;

    @Embedded
    private FileReferencePersistenceEmbeddable file;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;
}
