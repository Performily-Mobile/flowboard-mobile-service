package com.performily.flowboard.payroll.application.internal.outboundservices.storage;

import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;

import java.time.Duration;

/**
 * Payslip File Storage Service
 * @summary
 * Port to the storage service where the payslip PDFs are kept.
 * The implementation lives in infrastructure/storage.
 *
 * @since 1.0.0
 */
public interface PayslipFileStorageService {
    /**
     * Generates a temporary URL to download a file.
     *
     * @param file     the stored file
     * @param validity how long the URL works
     * @return the temporary URL
     */
    String generateTemporaryDownloadUrl(FileReference file, Duration validity);
}
