package com.performily.flowboard.payroll.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Replace Payslip File Command
 * @summary
 * Command to replace the file of a payslip. The payslip goes back to UNDER_REVIEW.
 *
 * @since 1.0.0
 */
public record ReplacePayslipFileCommand(Long payslipId, String fileName, String contentType, Long sizeInBytes, String storageUrl, LocalDate issueDate, BigDecimal netAmount, String currency) {
    /**
     * Compact constructor for ReplacePayslipFileCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public ReplacePayslipFileCommand {
        if (payslipId == null || payslipId <= 0) {
            throw new IllegalArgumentException("payslipId cannot be null or less than 1");
        }
        if (sizeInBytes == null || issueDate == null || netAmount == null) {
            throw new IllegalArgumentException("sizeInBytes, issueDate and netAmount are required");
        }
    }
}
