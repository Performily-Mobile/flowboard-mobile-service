package com.performily.flowboard.workspace.domain.model.valueobjects;

import com.performily.flowboard.shared.domain.model.valueobjects.EmailAddress;

/**
 * Contact Info Value Object
 * @summary
 * E-mail address and phone number of the employee.
 *
 * @param email       the e-mail address
 * @param phoneNumber the phone number
 * @since 1.0.0
 */
public record ContactInfo(EmailAddress email, PhoneNumber phoneNumber) {
    /**
     * Compact constructor for ContactInfo.
     *
     * @throws IllegalArgumentException if the e-mail or the phone number is null
     */
    public ContactInfo {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (phoneNumber == null) {
            throw new IllegalArgumentException("Phone number cannot be null");
        }
    }
}