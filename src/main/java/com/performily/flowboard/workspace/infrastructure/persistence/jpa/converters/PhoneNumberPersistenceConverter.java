package com.performily.flowboard.workspace.infrastructure.persistence.jpa.converters;

import com.performily.flowboard.workspace.domain.model.valueobjects.PhoneNumber;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Phone Number Persistence Converter
 * @summary
 * Converts phone numbers between the domain model and persistence column values.
 *
 * @since 1.0.0
 */
@Converter(autoApply = false)
public class PhoneNumberPersistenceConverter implements AttributeConverter<PhoneNumber, String> {
    @Override
    public String convertToDatabaseColumn(PhoneNumber attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public PhoneNumber convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new PhoneNumber(dbData);
    }
}