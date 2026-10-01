package com.performily.flowboard.shared.infrastructure.persistence.jpa.converters;

import com.performily.flowboard.shared.domain.model.valueobjects.EmailAddress;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Email Address Persistence Converter
 * @summary
 * Converts e-mail addresses between the domain model and persistence column values.
 *
 * @since 1.0.0
 */
@Converter(autoApply = false)
public class EmailAddressPersistenceConverter implements AttributeConverter<EmailAddress, String> {
    @Override
    public String convertToDatabaseColumn(EmailAddress attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public EmailAddress convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new EmailAddress(dbData);
    }
}