package com.performily.flowboard.payroll.infrastructure.persistence.jpa.assemblers;

import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;
import com.performily.flowboard.shared.domain.model.valueobjects.Money;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.embeddables.FileReferencePersistenceEmbeddable;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.embeddables.MoneyPersistenceEmbeddable;
import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.domain.model.valueobjects.PaymentDetails;
import com.performily.flowboard.payroll.domain.model.valueobjects.PaymentStatus;
import com.performily.flowboard.payroll.infrastructure.persistence.jpa.embeddables.PaymentDetailsPersistenceEmbeddable;
import com.performily.flowboard.payroll.infrastructure.persistence.jpa.entities.PayrollPeriodPersistenceEntity;
import com.performily.flowboard.payroll.infrastructure.persistence.jpa.entities.PayslipPersistenceEntity;

import java.util.Currency;

/**
 * Payslip Persistence Assembler
 * @summary
 * Static assembler between payslip domain and persistence representations.
 *
 * @since 1.0.0
 */
public final class PayslipPersistenceAssembler {
    private static final String DEFAULT_CONTENT_TYPE = "application/pdf";

    private PayslipPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity to its domain representation.
     *
     * @param entity the {@link PayslipPersistenceEntity} instance
     * @return the domain object, or null when the entity is null
     */
    public static Payslip toDomainFromPersistence(PayslipPersistenceEntity entity) {
        if (entity == null) return null;
        return new Payslip(
                entity.getId(),
                new EmployeeId(entity.getEmployeeId()),
                PayrollPeriodPersistenceAssembler.toDomainFromPersistence(entity.getPayrollPeriod()),
                toFileReference(entity.getFile()),
                entity.getIssueDate(),
                toMoney(entity.getNetAmount()),
                entity.getPublicationStatus(),
                entity.getPublishedAt(),
                toPaymentDetails(entity.getPayment()));
    }

    /**
     * Converts a domain object to its persistence representation.
     *
     * @param payslip       the {@link Payslip} instance
     * @param payrollPeriod the {@link PayrollPeriodPersistenceEntity} the payslip belongs to
     * @return the persistence entity, or null when the domain object is null
     */
    public static PayslipPersistenceEntity toPersistenceFromDomain(Payslip payslip,
                                                                   PayrollPeriodPersistenceEntity payrollPeriod) {
        if (payslip == null) return null;
        var entity = new PayslipPersistenceEntity();
        if (payslip.getId() != null) {
            entity.setId(payslip.getId());
        }
        entity.setEmployeeId(payslip.getEmployeeId().value());
        entity.setPayrollPeriod(payrollPeriod);
        var file = payslip.getFile();
        entity.setFile(new FileReferencePersistenceEmbeddable(
                file.fileName(), file.contentType(), file.sizeInBytes(), file.storageUrl()));
        entity.setIssueDate(payslip.getIssueDate());
        entity.setNetAmount(new MoneyPersistenceEmbeddable(
                payslip.getNetAmount().amount(), payslip.getNetAmount().currency().getCurrencyCode()));
        entity.setPublicationStatus(payslip.getPublicationStatus());
        entity.setPublishedAt(payslip.getPublishedAt());
        var payment = payslip.getPayment();
        entity.setPayment(new PaymentDetailsPersistenceEmbeddable(
                payment.status(), payment.paidOn(), payment.observationReason()));
        return entity;
    }

    /*
     * Payslips loaded before this version may not have content type or size.
     * They are read as PDF files so the list does not fail.
     */
    private static FileReference toFileReference(FileReferencePersistenceEmbeddable file) {
        var contentType = file.getContentType() == null || file.getContentType().isBlank()
                ? DEFAULT_CONTENT_TYPE : file.getContentType();
        var size = file.getSizeBytes() == null || file.getSizeBytes() <= 0 ? 1L : file.getSizeBytes();
        return new FileReference(file.getFileName(), contentType, size, file.getStorageUrl());
    }

    private static Money toMoney(MoneyPersistenceEmbeddable money) {
        var currency = money.getCurrency() == null || money.getCurrency().isBlank()
                ? Money.DEFAULT_CURRENCY : Currency.getInstance(money.getCurrency());
        return new Money(money.getAmount(), currency);
    }

    private static PaymentDetails toPaymentDetails(PaymentDetailsPersistenceEmbeddable payment) {
        if (payment == null || payment.getStatus() == null || payment.getStatus() == PaymentStatus.PENDING) {
            return PaymentDetails.pending();
        }
        try {
            return new PaymentDetails(payment.getStatus(), payment.getPaidOn(), payment.getObservationReason());
        } catch (IllegalArgumentException e) {
            // Rows loaded by hand without payment date or reason are read as pending.
            return PaymentDetails.pending();
        }
    }
}
