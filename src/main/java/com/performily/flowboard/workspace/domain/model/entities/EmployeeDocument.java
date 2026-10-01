package com.performily.flowboard.workspace.domain.model.entities;

import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;
import com.performily.flowboard.workspace.domain.model.valueobjects.DocumentType;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Employee Document Entity
 * @summary
 * A file attached to the employee record. Only DocumentType values are accepted,
 * and only PDF, JPG or PNG files up to the configured maximum size.
 *
 * @since 1.0.0
 */
public class EmployeeDocument {
    /**
     * Content types accepted for employee documents.
     */
    public static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("application/pdf", "image/jpeg", "image/png");

    /**
     * Maximum file size accepted for employee documents (5 MB).
     */
    public static final long MAX_SIZE_IN_BYTES = 5L * 1024 * 1024;

    private Long id;
    private DocumentType documentType;
    private FileReference file;
    private LocalDateTime uploadedAt;

    /**
     * Creates a new employee document.
     *
     * @param documentType the document type
     * @param file         the file reference
     */
    public EmployeeDocument(DocumentType documentType, FileReference file) {
        this(null, documentType, file, LocalDateTime.now());
        if (!ALLOWED_CONTENT_TYPES.contains(file.contentType().toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Only PDF, JPG or PNG files are accepted");
        }
        if (file.sizeInBytes() > MAX_SIZE_IN_BYTES) {
            throw new IllegalArgumentException("File size cannot exceed %d bytes".formatted(MAX_SIZE_IN_BYTES));
        }
    }

    /**
     * Rebuilds an existing employee document. Used by the persistence assemblers.
     *
     * @param id           the document id
     * @param documentType the document type
     * @param file         the file reference
     * @param uploadedAt   the upload date and time
     */
    public EmployeeDocument(Long id, DocumentType documentType, FileReference file, LocalDateTime uploadedAt) {
        this.id = id;
        this.documentType = Objects.requireNonNull(documentType, "Document type cannot be null");
        this.file = Objects.requireNonNull(file, "File cannot be null");
        this.uploadedAt = Objects.requireNonNull(uploadedAt, "Upload date cannot be null");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public FileReference getFile() {
        return file;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}