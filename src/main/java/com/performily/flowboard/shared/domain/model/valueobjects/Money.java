package com.performily.flowboard.shared.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * Money Value Object
 * @summary
 * Amount greater than or equal to 0 with scale 2. Operations are only allowed
 * between amounts of the same currency. The default currency is PEN.
 *
 * @param amount   the amount
 * @param currency the ISO 4217 currency
 * @since 1.0.0
 */
public record Money(BigDecimal amount, Currency currency) {
    /**
     * Default currency of the platform.
     */
    public static final Currency DEFAULT_CURRENCY = Currency.getInstance("PEN");

    /**
     * Compact constructor for Money.
     *
     * @throws IllegalArgumentException if the amount is null or negative
     */
    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (currency == null) {
            currency = DEFAULT_CURRENCY;
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Creates an amount in the default currency.
     *
     * @param amount the amount
     */
    public Money(BigDecimal amount) {
        this(amount, DEFAULT_CURRENCY);
    }

    /**
     * Creates a zero amount in the given currency.
     *
     * @param currency the currency
     * @return a zero Money
     */
    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    /**
     * Adds another amount of the same currency.
     *
     * @param other the amount to add
     * @return a new Money with the sum
     */
    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Checks whether this amount is greater than another amount of the same currency.
     *
     * @param other the amount to compare
     * @return true if this amount is greater
     */
    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    private void requireSameCurrency(Money other) {
        if (other == null || !this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Operations are only allowed between the same currency");
        }
    }
}