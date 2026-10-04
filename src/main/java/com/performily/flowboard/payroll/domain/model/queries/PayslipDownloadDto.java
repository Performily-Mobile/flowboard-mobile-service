package com.performily.flowboard.payroll.domain.model.queries;

public record PayslipDownloadDto(String downloadUrl, String fileName, String contentType) {}