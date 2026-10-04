package com.performily.flowboard.payroll.domain.services;

import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipByIdAndEmployeeIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipDownloadUrlQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipsByEmployeeIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.PayslipDownloadDto;

import java.util.List;
import java.util.Optional;

public interface PayrollQueryService {
    List<Payslip> handle(GetPayslipsByEmployeeIdQuery query);
    Optional<Payslip> handle(GetPayslipByIdAndEmployeeIdQuery query);
    PayslipDownloadDto handle(GetPayslipDownloadUrlQuery query);
}