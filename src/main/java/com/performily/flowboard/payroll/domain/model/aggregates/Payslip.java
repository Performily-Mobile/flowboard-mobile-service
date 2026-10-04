package com.performily.flowboard.payroll.domain.model.aggregates;

import com.performily.flowboard.payroll.domain.model.valueobjects.PaymentStatus;
import com.performily.flowboard.payroll.domain.model.valueobjects.PublicationStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payslips")
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payroll_period_id", nullable = false)
    private Integer payrollPeriodId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size")
    private Integer fileSize;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "net_amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal netAmount;

    @Column(name = "gross_salary", columnDefinition = "DECIMAL(10,2)")
    private Double grossSalary;

    @Column(name = "deductions", columnDefinition = "DECIMAL(10,2)")
    private Double deductions;

    @Column(name = "currency", length = 3)
    private String currency = "PEN";

    @Enumerated(EnumType.STRING)
    @Column(name = "publication_status", nullable = false)
    private PublicationStatus publicationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "paid_on")
    private LocalDateTime paidOn;

    @Column(name = "observation_reason", columnDefinition = "TEXT")
    private String observationReason;

    // Getters
    public Long getId() { return id; }
    public Integer getPayrollPeriodId() { return payrollPeriodId; }
    public Long getEmployeeId() { return employeeId; }
    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public Integer getFileSize() { return fileSize; }
    public String getFileUrl() { return fileUrl; }
    public LocalDate getIssueDate() { return issueDate; }
    public BigDecimal getNetAmount() { return netAmount; }
    public Double getGrossSalary() { return grossSalary; }
    public Double getDeductions() { return deductions; }
    public String getCurrency() { return currency; }
    public PublicationStatus getPublicationStatus() { return publicationStatus; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public LocalDateTime getPaidOn() { return paidOn; }
    public String getObservationReason() { return observationReason; }
    
    // Regla de negocio: Validar si la boleta es visible para un empleado específico
    public boolean isVisibleTo(Long requesterEmployeeId) {
        return this.publicationStatus == PublicationStatus.PUBLISHED && 
               this.employeeId.equals(requesterEmployeeId);
    }
}