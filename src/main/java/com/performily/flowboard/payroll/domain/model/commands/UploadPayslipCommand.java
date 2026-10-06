package com.performily.flowboard.payroll.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Upload Payslip Command
 * @summary
 * Command to upload the payslip of an employee for a payroll period. The file was already uploaded to the storage service by the client; this command registers its metadata.
 *
 * @since 1.0.0
 */
public record UploadPayslipCommand(Long employeeId, Long payrollPeriodId, String fileName, String contentType, Long sizeInBytes, String storageUrl, LocalDate issueDate, BigDecimal netAmount, String currency) {
    /**
     * Compact constructor for UploadPayslipCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public UploadPayslipCommand {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
        if (payrollPeriodId == null || payrollPeriodId <= 0) {
            throw new IllegalArgumentException("payrollPeriodId cannot be null or less than 1");
        }
        if (sizeInBytes == null || issueDate == null || netAmount == null) {
            throw new IllegalArgumentException("sizeInBytes, issueDate and netAmount are required");
        }
    }
}
