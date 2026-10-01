package com.performily.flowboard.workspace.infrastructure.persistence.jpa.converters;

import com.performily.flowboard.workspace.domain.model.valueobjects.BirthDate;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;

/**
 * Birth Date Persistence Converter
 * @summary
 * Converts birth dates between the domain model and persistence column values.
 *
 * @since 1.0.0
 */
@Converter(autoApply = false)
public class BirthDatePersistenceConverter implements AttributeConverter<BirthDate, LocalDate> {
    @Override
    public LocalDate convertToDatabaseColumn(BirthDate attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public BirthDate convertToEntityAttribute(LocalDate dbData) {
        return dbData == null ? null : new BirthDate(dbData);
    }
}