package com.performily.flowboard.payroll.domain.model.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payroll_periods")
public class PayrollPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "period_year", nullable = false)
    private Integer periodYear;

    @Column(name = "period_month", nullable = false)
    private Integer periodMonth;

    @Column(name = "scheduled_payment_date")
    private LocalDate scheduledPaymentDate;

    // Getters necesarios para que JPA pueda leer los datos
    public Integer getId() { return id; }
    public Integer getPeriodYear() { return periodYear; }
    public Integer getPeriodMonth() { return periodMonth; }
    public LocalDate getScheduledPaymentDate() { return scheduledPaymentDate; }
}