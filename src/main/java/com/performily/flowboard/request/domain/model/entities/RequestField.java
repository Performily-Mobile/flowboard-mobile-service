package com.performily.flowboard.request.domain.model.entities;

import com.performily.flowboard.request.domain.model.valueobjects.FieldDataType;
import com.performily.flowboard.request.domain.model.valueobjects.FieldKey;

import java.util.Objects;

/**
 * Request Field Entity
 * @summary
 * Definition of one field of the dynamic form of a request type: key, label,
 * data type, whether it is required and the order in which the app shows it.
 *
 * @since 1.0.0
 */
public class RequestField {
    private static final int LABEL_MAX_LENGTH = 80;

    private Long id;
    private FieldKey key;
    private String label;
    private FieldDataType dataType;
    private boolean required;
    private int displayOrder;

    /**
     * Creates a new field.
     *
     * @param key          the field key
     * @param label        the label shown in the form
     * @param dataType     the data type
     * @param required     whether the field is required
     * @param displayOrder the order in the form
     */
    public RequestField(FieldKey key, String label, FieldDataType dataType, boolean required, int displayOrder) {
        this(null, key, label, dataType, required, displayOrder);
    }

    /**
     * Rebuilds an existing field. Used by the persistence assemblers.
     */
    public RequestField(Long id, FieldKey key, String label, FieldDataType dataType, boolean required, int displayOrder) {
        this.id = id;
        this.key = Objects.requireNonNull(key, "Field key cannot be null");
        this.label = validateLabel(label);
        this.dataType = Objects.requireNonNull(dataType, "Field data type cannot be null");
        this.required = required;
        if (displayOrder < 0) {
            throw new IllegalArgumentException("Display order cannot be negative");
        }
        this.displayOrder = displayOrder;
    }

    /**
     * Checks whether the value is valid for the data type of this field.
     *
     * @param value the value
     * @return true if it is valid
     */
    public boolean accepts(String value) {
        return dataType.accepts(value);
    }

    private static String validateLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Field label cannot be null or blank");
        }
        var trimmed = label.trim();
        if (trimmed.length() > LABEL_MAX_LENGTH) {
            throw new IllegalArgumentException("Field label cannot exceed %d characters".formatted(LABEL_MAX_LENGTH));
        }
        return trimmed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FieldKey getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public FieldDataType getDataType() {
        return dataType;
    }

    public boolean isRequired() {
        return required;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}
