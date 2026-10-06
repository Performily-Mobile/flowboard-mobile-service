package com.performily.flowboard.payroll.domain.model.entities;

import com.performily.flowboard.payroll.domain.model.valueobjects.PayPeriod;

import java.time.LocalDate;

/**
 * Payroll Period Entity
 * @summary
 * A month of payroll with its scheduled payment date. There is only one
 * PayrollPeriod per {@link PayPeriod}; that rule is checked by the application
 * service because it needs the repository.
 *
 * @since 1.0.0
 */
public class PayrollPeriod {
    private Long id;
    private PayPeriod period;
    private LocalDate scheduledPaymentDate;

    /**
     * Creates a new payroll period.
     *
     * @param period               the pay period
     * @param scheduledPaymentDate the scheduled payment date
     */
    public PayrollPeriod(PayPeriod period, LocalDate scheduledPaymentDate) {
        this(null, period, scheduledPaymentDate);
    }

    /**
     * Rebuilds an existing payroll period. Used by the persistence assemblers.
     *
     * @param id                   the id
     * @param period               the pay period
     * @param scheduledPaymentDate the scheduled payment date
     */
    public PayrollPeriod(Long id, PayPeriod period, LocalDate scheduledPaymentDate) {
        if (period == null) {
            throw new IllegalArgumentException("Pay period is required");
        }
        if (scheduledPaymentDate == null) {
            throw new IllegalArgumentException("Scheduled payment date is required");
        }
        if (scheduledPaymentDate.isBefore(period.toYearMonth().atDay(1))) {
            throw new IllegalArgumentException("Scheduled payment date cannot be before the pay period");
        }
        this.id = id;
        this.period = period;
        this.scheduledPaymentDate = scheduledPaymentDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PayPeriod getPeriod() {
        return period;
    }

    public LocalDate getScheduledPaymentDate() {
        return scheduledPaymentDate;
    }
}
