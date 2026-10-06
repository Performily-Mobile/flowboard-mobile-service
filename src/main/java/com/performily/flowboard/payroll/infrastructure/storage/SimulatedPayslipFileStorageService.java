package com.performily.flowboard.payroll.infrastructure.storage;

import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;
import com.performily.flowboard.payroll.application.internal.outboundservices.storage.PayslipFileStorageService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Simulated Payslip File Storage Service
 * @summary
 * TEMPORARY implementation of {@link PayslipFileStorageService}. It adds a fake
 * signature and expiration to the stored URL. When the team picks a real storage
 * (for example Firebase Storage or S3), a new implementation replaces this one and
 * nothing else changes.
 *
 * @since 1.0.0
 */
@Service
public class SimulatedPayslipFileStorageService implements PayslipFileStorageService {

    @Override
    public String generateTemporaryDownloadUrl(FileReference file, Duration validity) {
        var expiresAt = Instant.now().plus(validity).getEpochSecond();
        var separator = file.storageUrl().contains("?") ? "&" : "?";
        return file.storageUrl() + separator + "signature=" + UUID.randomUUID() + "&expires=" + expiresAt;
    }
}
