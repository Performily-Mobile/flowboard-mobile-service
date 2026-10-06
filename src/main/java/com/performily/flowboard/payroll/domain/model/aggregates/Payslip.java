package com.performily.flowboard.payroll.domain.model.aggregates;

import com.performily.flowboard.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;
import com.performily.flowboard.shared.domain.model.valueobjects.Money;
import com.performily.flowboard.payroll.domain.model.entities.PayrollPeriod;
import com.performily.flowboard.payroll.domain.model.events.PaymentMarkedAsPaidEvent;
import com.performily.flowboard.payroll.domain.model.events.PaymentObservedEvent;
import com.performily.flowboard.payroll.domain.model.events.PayslipPublishedEvent;
import com.performily.flowboard.payroll.domain.model.valueobjects.PaymentDetails;
import com.performily.flowboard.payroll.domain.model.valueobjects.PublicationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Payslip Aggregate Root
 * @summary
 * Payslip of an employee for a payroll period.
 *
 * Invariants checked by the aggregate:
 * - Repository and payment status only: nothing is calculated. The payslip is
 *   issued by the organization's payroll system and only the net amount is kept.
 * - A new payslip starts UNDER_REVIEW and PENDING.
 * - Only PUBLISHED payslips are visible, and only to their own employee.
 * - File: PDF only, up to {@link #MAX_FILE_SIZE_IN_BYTES}.
 * - The payment can only be marked as paid or observed after publication.
 *
 * "One payslip per employee per PayrollPeriod" needs the repository, so it is
 * checked by the application service.
 *
 * Events: PayslipPublished, PaymentMarkedAsPaid, PaymentObserved.
 *
 * @since 1.0.0
 */
public class Payslip extends AbstractDomainAggregateRoot<Payslip> {
    public static final long MAX_FILE_SIZE_IN_BYTES = 5L * 1024 * 1024;

    private Long id;
    private EmployeeId employeeId;
    private PayrollPeriod payrollPeriod;
    private FileReference file;
    private LocalDate issueDate;
    private Money netAmount;
    private PublicationStatus publicationStatus;
    private LocalDateTime publishedAt;
    private PaymentDetails payment;

    /**
     * Uploads a new payslip. It starts UNDER_REVIEW and PENDING.
     *
     * @param employeeId    the employee id
     * @param payrollPeriod the payroll period
     * @param file          the PDF file
     * @param issueDate     the issue date
     * @param netAmount     the net amount
     */
    public Payslip(EmployeeId employeeId, PayrollPeriod payrollPeriod, FileReference file,
                   LocalDate issueDate, Money netAmount) {
        this(null, employeeId, payrollPeriod, validateFile(file), issueDate, netAmount,
                PublicationStatus.UNDER_REVIEW, null, PaymentDetails.pending());
    }

    /**
     * Rebuilds an existing payslip. Used by the persistence assemblers.
     * The file rules are checked when a payslip is uploaded or replaced, not here,
     * so payslips stored before those rules can still be read.
     *
     * @param id                the id
     * @param employeeId        the employee id
     * @param payrollPeriod     the payroll period
     * @param file              the file
     * @param issueDate         the issue date
     * @param netAmount         the net amount
     * @param publicationStatus the publication status
     * @param publishedAt       when it was published, or null
     * @param payment           the payment details
     */
    public Payslip(Long id, EmployeeId employeeId, PayrollPeriod payrollPeriod, FileReference file,
                   LocalDate issueDate, Money netAmount, PublicationStatus publicationStatus,
                   LocalDateTime publishedAt, PaymentDetails payment) {
        this.id = id;
        this.employeeId = Objects.requireNonNull(employeeId, "Employee id cannot be null");
        this.payrollPeriod = Objects.requireNonNull(payrollPeriod, "Payroll period cannot be null");
        this.file = Objects.requireNonNull(file, "File cannot be null");
        this.issueDate = Objects.requireNonNull(issueDate, "Issue date cannot be null");
        this.netAmount = Objects.requireNonNull(netAmount, "Net amount cannot be null");
        this.publicationStatus = Objects.requireNonNull(publicationStatus, "Publication status cannot be null");
        this.publishedAt = publishedAt;
        this.payment = Objects.requireNonNull(payment, "Payment details cannot be null");
    }

    /**
     * Publishes the payslip so the employee can see it.
     */
    public void publish() {
        if (publicationStatus == PublicationStatus.PUBLISHED) {
            throw new IllegalStateException("Payslip is already published");
        }
        this.publicationStatus = PublicationStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        registerDomainEvent(new PayslipPublishedEvent(id, employeeId.value(),
                payrollPeriod.getPeriod().year(), payrollPeriod.getPeriod().month()));
    }

    /**
     * Replaces the file of the payslip. The payslip goes back to UNDER_REVIEW
     * so HR checks the new file before the employee sees it.
     *
     * @param file      the new PDF file
     * @param issueDate the new issue date
     * @param netAmount the new net amount
     */
    public void replaceFile(FileReference file, LocalDate issueDate, Money netAmount) {
        if (payment.isPaid()) {
            throw new IllegalStateException("A paid payslip cannot be replaced");
        }
        this.file = validateFile(file);
        this.issueDate = Objects.requireNonNull(issueDate, "Issue date cannot be null");
        this.netAmount = Objects.requireNonNull(netAmount, "Net amount cannot be null");
        this.publicationStatus = PublicationStatus.UNDER_REVIEW;
        this.publishedAt = null;
        this.payment = PaymentDetails.pending();
    }

    /**
     * Marks the payment as paid.
     *
     * @param paidOn the payment date
     */
    public void markAsPaid(LocalDate paidOn) {
        requirePublished();
        this.payment = PaymentDetails.paid(paidOn);
        registerDomainEvent(new PaymentMarkedAsPaidEvent(id, employeeId.value(), paidOn));
    }

    /**
     * Marks the payment as observed.
     *
     * @param reason the observation reason
     */
    public void markAsObserved(String reason) {
        requirePublished();
        this.payment = PaymentDetails.observed(reason);
        registerDomainEvent(new PaymentObservedEvent(id, employeeId.value(), this.payment.observationReason()));
    }

    /**
     * Checks whether the payslip is visible to an employee: it must be PUBLISHED
     * and belong to that employee.
     *
     * @param requesterId the employee that asks
     * @return true if visible
     */
    public boolean isVisibleTo(EmployeeId requesterId) {
        return publicationStatus == PublicationStatus.PUBLISHED && employeeId.equals(requesterId);
    }

    private void requirePublished() {
        if (publicationStatus != PublicationStatus.PUBLISHED) {
            throw new IllegalStateException("The payslip must be published first");
        }
    }

    private static FileReference validateFile(FileReference file) {
        Objects.requireNonNull(file, "File cannot be null");
        if (!file.isPdf()) {
            throw new IllegalArgumentException("The payslip must be a PDF file");
        }
        if (file.sizeInBytes() > MAX_FILE_SIZE_IN_BYTES) {
            throw new IllegalArgumentException("The payslip file cannot exceed 5 MB");
        }
        return file;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmployeeId getEmployeeId() {
        return employeeId;
    }

    public PayrollPeriod getPayrollPeriod() {
        return payrollPeriod;
    }

    public FileReference getFile() {
        return file;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public Money getNetAmount() {
        return netAmount;
    }

    public PublicationStatus getPublicationStatus() {
        return publicationStatus;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public PaymentDetails getPayment() {
        return payment;
    }
}
