package com.performily.flowboard.payroll.interfaces.rest.resources;

public record PayslipDownloadUrlResource(String downloadUrl, String fileName, String contentType) {}