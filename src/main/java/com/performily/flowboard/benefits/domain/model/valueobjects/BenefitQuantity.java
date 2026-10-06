package com.performily.flowboard.benefits.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Benefit Quantity Value Object
 * @summary
 * Amount of a benefit given to an employee. Its unit comes from the benefit type:
 * S/ 150.00 (MONEY), 1 day (DAYS) or 2 items (UNITS).
 *
 * Rules: value greater than 0 with at most 2 decimals. With UNITS it must be a whole number.
 *
 * @param value the amount, always stored with scale 2
 * @since 1.0.0
 */
public record BenefitQuantity(BigDecimal value) {

    /**
     * Compact constructor for BenefitQuantity.
     *
     * @throws IllegalArgumentException if the value is null, not positive or has more than 2 decimals
     */
    public BenefitQuantity {
        if (value == null) {
            throw new IllegalArgumentException("Benefit quantity is required");
        }
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("Benefit quantity must be greater than 0");
        }
        if (value.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Benefit quantity can have at most 2 decimals");
        }
        value = value.setScale(2, RoundingMode.UNNECESSARY);
    }

    /**
     * Checks whether the quantity can be expressed in a unit.
     * Units are whole items, so 1.5 units is not valid.
     *
     * @param unit the unit of the benefit type
     * @return true when the quantity is valid for that unit
     */
    public boolean isCompatibleWith(BenefitUnit unit) {
        return unit != BenefitUnit.UNITS || value.stripTrailingZeros().scale() <= 0;
    }
}
