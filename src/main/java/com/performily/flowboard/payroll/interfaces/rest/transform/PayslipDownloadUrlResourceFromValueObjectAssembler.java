package com.performily.flowboard.payroll.interfaces.rest.transform;

import com.performily.flowboard.payroll.domain.model.valueobjects.PayslipDownloadLink;
import com.performily.flowboard.payroll.interfaces.rest.resources.PayslipDownloadUrlResource;

/**
 * Payslip Download Url Resource From Value Object Assembler
 * @summary
 * Assembler to convert a PayslipDownloadLink to a PayslipDownloadUrlResource.
 *
 * @since 1.0.0
 */
public class PayslipDownloadUrlResourceFromValueObjectAssembler {
    /**
     * Converts a {@link PayslipDownloadLink} to a {@link PayslipDownloadUrlResource}.
     *
     * @param link the {@link PayslipDownloadLink} instance
     * @return the {@link PayslipDownloadUrlResource}
     */
    public static PayslipDownloadUrlResource toResourceFromValueObject(PayslipDownloadLink link) {
        return new PayslipDownloadUrlResource(link.downloadUrl(), link.fileName(), link.contentType(), link.expiresAt());
    }
}
