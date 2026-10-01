package com.performily.flowboard.shared.domain.model.valueobjects;

/**
 * File Reference Value Object
 * @summary
 * Metadata of a stored file. fileName, contentType and storageUrl are not blank
 * and sizeInBytes is greater than 0. Each bounded context sets its own allowed
 * types and maximum size.
 *
 * @param fileName    the original file name
 * @param contentType the MIME type of the file
 * @param sizeInBytes the size of the file in bytes
 * @param storageUrl  the URL where the file is stored
 * @since 1.0.0
 */
public record FileReference(String fileName, String contentType, long sizeInBytes, String storageUrl) {
    /**
     * Compact constructor for FileReference.
     *
     * @throws IllegalArgumentException if a text field is blank or the size is not positive
     */
    public FileReference {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be null or blank");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content type cannot be null or blank");
        }
        if (sizeInBytes <= 0) {
            throw new IllegalArgumentException("File size must be greater than 0");
        }
        if (storageUrl == null || storageUrl.isBlank()) {
            throw new IllegalArgumentException("Storage URL cannot be null or blank");
        }
    }

    /**
     * Checks whether the file is a PDF document.
     *
     * @return true if the content type is application/pdf
     */
    public boolean isPdf() {
        return "application/pdf".equalsIgnoreCase(contentType);
    }
}